package com.example.contactlist;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.icu.text.Transliterator;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MyCustomAdepter extends RecyclerView.Adapter<MyViewHolder> implements Filterable {

    Context context;
    List<ModelClass> arraylist;
    List<ModelClass> searchArraylist;
    // List<ModelClass>nomanarraylist;
    DatabaseHelper databaseHelper;

    public MyCustomAdepter(Context context, List<ModelClass> arraylist) {
        this.context = context;
        this.arraylist = arraylist;
        searchArraylist = new ArrayList<>(arraylist);
        databaseHelper = new DatabaseHelper(context);

    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.sample_layout, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);

        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {

        holder.Name.setText(arraylist.get(position).getName());
        holder.Number.setText(arraylist.get(position).getNumber());
        holder.Date.setText(arraylist.get(position).getDate());
        holder.Number.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                copyMethod(position);
            }
        });
        holder.smsImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                String number = arraylist.get(position).getNumber();
                intent.setData(Uri.parse("sms:" + number));
                //String message="It's my sms";
                // intent.putExtra("sms_body",message);
                context.startActivity(intent);
            }
        });
        holder.callImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                String number = arraylist.get(position).getNumber();
                intent.setData(Uri.parse("tel:" + number));

                context.startActivity(intent);
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                operationMethod(position);
                return true;
            }
        });


    }

    private void copyMethod(int position) {
        String name = String.valueOf(arraylist.get(position).getNumber());
        ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("noman", name);
        clipboardManager.setPrimaryClip(clip);
        Toast.makeText(context, "Copied!", Toast.LENGTH_SHORT).show();
    }

    private void operationMethod(final int position) {
        final int Position = position;
        TextView updateTextView, deleteTextView, copyTextView;
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.alert_selection_layout, null, false);
        builder.setView(view);
        final AlertDialog alertDialog = builder.create();
        updateTextView = view.findViewById(R.id.updateTextViewId);
        deleteTextView = view.findViewById(R.id.deleteTextViewId);
        copyTextView = view.findViewById(R.id.copyNumberTextViewId);
        updateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateMethod(position);
                alertDialog.dismiss();
            }
        });

        deleteTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteMethod(Position);
                alertDialog.dismiss();

            }
        });
        copyTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                copyMethod(position);
                alertDialog.dismiss();
            }
        });


        alertDialog.show();


    }

    private void updateMethod(final int Position) {

        final EditText contuctEditText, phoneEditText;
        Button okButton;
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.dialoge_layout, null, false);
        builder.setView(view);
        final androidx.appcompat.app.AlertDialog alertDialog = builder.create();
        contuctEditText = view.findViewById(R.id.contactNameId);
        phoneEditText = view.findViewById(R.id.phoneNumberId);
        contuctEditText.setText(arraylist.get(Position).getName());
        phoneEditText.setText(arraylist.get(Position).getNumber());
        okButton = view.findViewById(R.id.okButtonId);
        okButton.setOnClickListener(new View.OnClickListener() {
            String contuctName, contuctNumber;

            @Override
            public void onClick(View v) {
                contuctName = contuctEditText.getText().toString();
                contuctNumber = phoneEditText.getText().toString();

                if (contuctName.isEmpty()) {
                    contuctEditText.setError("Enter Name");
                    contuctEditText.requestFocus();
                    return;
                } else if (contuctNumber.isEmpty()) {
                    contuctEditText.setError("Enter number");
                    contuctEditText.requestFocus();
                    return;
                }

                MainActivity mainActivity = new MainActivity();

                String Date = mainActivity.CurrentDate();

                long id = databaseHelper.updateData(new ModelClass(arraylist.get(Position).getId(), contuctName, contuctNumber, Date));
                if (id == 1) {
                    Toast.makeText(context, "Update Contuct", Toast.LENGTH_SHORT).show();
                    arraylist.clear();
                    arraylist.addAll(databaseHelper.readData());
                    notifyDataSetChanged();
                    alertDialog.dismiss();
                } else {
                    Toast.makeText(context, "No update", Toast.LENGTH_SHORT).show();
                    alertDialog.dismiss();
                }


            }

        });


        alertDialog.show();


    }

    private void deleteMethod(int Position) {
        final int position = Position;
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Alert!");
        builder.setIcon(R.drawable.alert);
        builder.setMessage("Are you sure you want to delete this contact ?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                int id = databaseHelper.deleteData(arraylist.get(position).getId());
                if (id == 1) {
                    Toast.makeText(context, "Deleted!", Toast.LENGTH_SHORT).show();
                    arraylist.remove(position);
                    searchArraylist.remove(position);
                    notifyDataSetChanged();
                    // nomanarraylist.addAll(arraylist);
                    // nomanarraylist=new ArrayList<>(arraylist);

                } else {
                    Toast.makeText(context, "Not delete!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("No", null);
        builder.show();

    }

    @Override
    public int getItemCount() {
        return arraylist.size();
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    public Filter filter = new Filter() {

        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {

            List<ModelClass> arraylist = new ArrayList<>();

            if (charSequence == null || charSequence.length() == 0) {

                arraylist.addAll(searchArraylist);

            } else {

                String value = charSequence.toString().toLowerCase().trim();
                for (ModelClass m : searchArraylist) {

                    if (m.getName().toLowerCase().trim().contains(value)) {

                        arraylist.add(m);
                    }

                }


            }
            FilterResults filterResults = new FilterResults();
            filterResults.values = arraylist;

            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            arraylist.clear();
            arraylist.addAll((List) results.values);
            notifyDataSetChanged();

        }
    };

}


class MyViewHolder extends RecyclerView.ViewHolder {
    TextView Name, Number, Date;
    ImageView callImageView, smsImageView;

    public MyViewHolder(@NonNull View itemView) {
        super(itemView);
        Name = itemView.findViewById(R.id.nameTextViewId);
        Number = itemView.findViewById(R.id.numberTextViewId);
        Date = itemView.findViewById(R.id.dateTextViewId);
        callImageView = itemView.findViewById(R.id.callImageViewId);
        smsImageView = itemView.findViewById(R.id.smsImageViewId);


    }
}