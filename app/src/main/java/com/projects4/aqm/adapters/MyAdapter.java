package com.projects4.aqm.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.projects4.aqm.FloorsList;
import com.projects4.aqm.R;
import com.projects4.aqm.RoomMonitor;
import com.projects4.aqm.UpdateRoom;
import com.projects4.aqm.controller.RoomDaoImplementation;
import com.projects4.aqm.dto.Room;

import java.sql.SQLException;
import java.util.LinkedList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    private final LinkedList<Room> rooms;
    private final Context context;

    public MyAdapter(LinkedList<Room> rooms, Context context) {
        this.rooms = new LinkedList<>() ;
        this.rooms.addAll(rooms);
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemLayoutView =
                LayoutInflater.from(parent.getContext()).inflate(R.layout.room_layout,
                        parent, false);
        return new MyViewHolder(itemLayoutView );
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        String ms1 = "Capacity : " + rooms.get(position).getCapacity() + " persons",
                ms2 = "Size : " + rooms.get(position).getSize() + " meters squared";
        holder.room = rooms.get(position);
        holder.identification.setText(rooms.get(position).getTitle());
        holder.capacity.setText(ms1);
        holder.size.setText(ms2);
    }

    @Override
    public int getItemCount() {
        return rooms.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        Room room;
        public TextView identification, capacity, size;
        RoomDaoImplementation impl = new RoomDaoImplementation();

        public MyViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            identification = itemLayoutView.findViewById(R.id.identification);
            capacity = itemLayoutView.findViewById(R.id.capacity);
            size = itemLayoutView.findViewById(R.id.size);
            itemLayoutView.setOnClickListener(this);
            itemLayoutView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(context, RoomMonitor.class);
            intent.putExtra("id", room.getId());
            intent.putExtra("title", room.getTitle());
            intent.putExtra("capacity", room.getCapacity());
            intent.putExtra("size", room.getSize());
            context.startActivity(intent);
        }

        @Override
        public boolean onLongClick(View view) {
            PopupMenu popup = new PopupMenu(context, view);
            MenuInflater inflater = popup.getMenuInflater();
            inflater.inflate(R.menu.room_popup_menu, popup.getMenu());
            popup.setOnMenuItemClickListener(item -> {
                if (item.getItemId()==R.id.menu_view){
                    Toast.makeText(context, "Viewing element ...", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(context, RoomMonitor.class);
                    intent.putExtra("id", room.getId());
                    intent.putExtra("title", room.getTitle());
                    intent.putExtra("capacity", room.getCapacity());
                    intent.putExtra("size", room.getSize());
                    context.startActivity(intent);
                }
                else if (item.getItemId() == R.id.menu_edit){
                    Toast.makeText(context, "Updating element ...", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(context, UpdateRoom.class);
                    intent.putExtra("id", room.getId());
                    intent.putExtra("title", room.getTitle());
                    intent.putExtra("capacity", room.getCapacity());
                    intent.putExtra("size", room.getSize());
                    context.startActivity(intent);
                }
                else if (item.getItemId() == R.id.menu_delete){
                    Toast.makeText(context, "Deleting element ...", Toast.LENGTH_SHORT).show();
                    try {
                        impl.delete(room.getId());
                        context.startActivity(new Intent(context, FloorsList.class));
                    } catch (SQLException e) {
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                return false;
            });
            popup.show();
            return true;
        }

    }
}
