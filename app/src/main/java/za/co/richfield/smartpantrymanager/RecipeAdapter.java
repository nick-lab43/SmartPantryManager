package za.co.richfield.smartpantrymanager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeHolder> {
    public interface Listener { void onSelect(Recipe recipe); }
    private final Listener listener;
    private final List<Recipe> recipes = new ArrayList<>();
    public RecipeAdapter(Listener listener) { this.listener = listener; }
    public void setRecipes(List<Recipe> data) { recipes.clear(); recipes.addAll(data); notifyDataSetChanged(); }
    @NonNull @Override public RecipeHolder onCreateViewHolder(@NonNull ViewGroup parent, int type) {
        return new RecipeHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recipe, parent, false));
    }
    @Override public void onBindViewHolder(@NonNull RecipeHolder h, int position) {
        Recipe recipe = recipes.get(position);
        h.name.setText(recipe.getName()); h.description.setText(recipe.getDescription());
        h.itemView.setOnClickListener(v -> listener.onSelect(recipe));
    }
    @Override public int getItemCount() { return recipes.size(); }
    static class RecipeHolder extends RecyclerView.ViewHolder {
        TextView name, description;
        RecipeHolder(View v) { super(v); name=v.findViewById(R.id.txtRecipeName); description=v.findViewById(R.id.txtRecipeDescription); }
    }
}
