package com.darincritchlow.assignment9.cs3270a9;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.darincritchlow.assignment9.cs3270a9.CanvasObjects.Assignment;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class AssignmentAdapter extends ArrayAdapter<Assignment> {

    public AssignmentAdapter(Context context, List<Assignment> assignments) {
        super(context, R.layout.item_assignment, assignments);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){

        Assignment assignment = getItem(position);

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_assignment, parent, false);
        }

        TextView txvAssignmentName = (TextView) convertView.findViewById(R.id.txvAssignmentName);
        TextView txvDueDate = (TextView) convertView.findViewById(R.id.txvDueDate);

        String dueDate = "";
        if(assignment.due_at != null) {
            try {
                SimpleDateFormat source = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
                source.setTimeZone(TimeZone.getTimeZone("UTC"));
                Date parsed = source.parse(assignment.due_at);
                DateFormat df = DateFormat.getDateTimeInstance();
                df.setTimeZone(TimeZone.getDefault());
                dueDate = df.format(parsed);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        txvAssignmentName.setText(assignment.name);
        txvDueDate.setText(dueDate);

        return convertView;
    }
}
