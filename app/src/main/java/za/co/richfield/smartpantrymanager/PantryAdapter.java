package za.co.richfield.smartpantrymanager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class PantryAdapter extends RecyclerView.Adapter<PantryAdapter.ItemHolder> {
    public interface Listener {
        void onEdit(PantryItem item);
        void onDelete(PantryItem item);
    }
    private final Listener listener;
    private final List<PantryItem> items = new ArrayList<>();

    public PantryAdapter(Listener listener) { this.listener = listener; }
    public void setItems(List<PantryItem> data) { items.clear(); items.addAll(data); notifyDataSetChanged(); }

    @NonNull @Override public ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ItemHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pantry, parent, false));
    }
    @Override public void onBindViewHolder(@NonNull ItemHolder h, int position) {
        PantryItem item = items.get(position);
        h.name.setText(item.getName());
        h.quantity.setText("Quantity: " + item.getQuantity() + " " + item.getUnit());
        h.expiry.setText(item.getExpiryDate().isEmpty() ? "Expiry: not supplied" : "Expiry: " + item.getExpiryDate());
        h.edit.setOnClickListener(v -> listener.onEdit(item));
        h.delete.setOnClickListener(v -> listener.onDelete(item));
    }
    @Override public int getItemCount() { return items.size(); }

    static class ItemHolder extends RecyclerView.ViewHolder {
        TextView name, quantity, expiry; Button edit, delete;
        ItemHolder(View v) {
            super(v); name=v.findViewById(R.id.txtItemName); quantity=v.findViewById(R.id.txtItemQuantity);
            expiry=v.findViewById(R.id.txtItemExpiry); edit=v.findViewById(R.id.btnEdit); delete=v.findViewById(R.id.btnDelete);
        }
    }
}
