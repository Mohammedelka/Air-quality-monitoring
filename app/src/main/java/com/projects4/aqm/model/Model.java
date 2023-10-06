package com.projects4.aqm.model;

import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.projects4.aqm.dto.Room;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

public class Model {
    FirebaseFirestore db;

    public Model(){
        db = FirebaseFirestore.getInstance();
    }

    public Room get(String id) {
        Task<DocumentSnapshot> task = db.collection("rooms").document(id).get();
        try {
            DocumentSnapshot doc = Tasks.await(task);
            if (doc.exists()) {
                return new Room(id,
                        doc.getString("title"),
                        doc.getString("capacity"),
                        doc.getString("size"));
            } else {
                Log.d("not ok", "Document does not exist");
            }
        } catch (ExecutionException | InterruptedException e) {
            Log.w("not ok", "Error finding document", e);
        }
        return null;
    }


    public List<Room> getAll() {
        List<Room> rooms = new ArrayList<>();
        final boolean[] terminated = new boolean[1];

        db.collection("rooms").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            rooms.add(
                                    new Room(
                                            document.getId(),
                                            Objects.requireNonNull(document.get("title")).toString(),
                                            Objects.requireNonNull(document.get("capacity")).toString(),
                                            Objects.requireNonNull(document.get("size")).toString()
                                    )
                            );
                        }
                        terminated[0] = true;
                    }
                    else
                        Log.d("not ok", "Error getting documents: ", task.getException());
                });

        while (!terminated[0])
            System.out.println("...");

        return rooms;
    }

    public void insert(Room room){
        Map<String, Object> r = new HashMap<>();
        r.put("title", room.getTitle());
        r.put("capacity", room.getCapacity());
        r.put("size", room.getSize());
        db.collection("rooms")
                .add(r)
                .addOnSuccessListener(documentReference -> Log.w("ok", "Added Successfully"))
                .addOnFailureListener(e -> Log.w("not ok", "Error adding document", e));
    }

    public void update(Room room) {

        Map<String, Object> updates = new HashMap<>();
        updates.put("title", room.getTitle());
        updates.put("capacity", room.getCapacity());
        updates.put("size", room.getSize());

        db.collection("rooms")
                .document(room.getId())
                .update(updates)
                .addOnSuccessListener(unused -> Log.d("ok", "Fields updated successfully"))
                .addOnFailureListener(e -> Log.w("not ok", "Error updating fields", e));
    }

    public void delete(String id) {
        db.collection("rooms").document(id).delete();
    }


}
