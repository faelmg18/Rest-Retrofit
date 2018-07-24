package com.br.rhf.testapirestapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.br.rhf.testapirestapp.model.Item;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;


public class GitAdapter extends BaseAdapter {

    private List<Item> list;
    private Context context;
    private ImageLoader imageLoader = ImageLoader.getInstance();


    public GitAdapter(List<Item> l, Context c) {
        this.list = l;
        this.context = c;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.git_repository_item_adapter, null);

        final Item item = list.get(position);

        TextView textViewRepositoryName = view.findViewById(R.id.text_view_repository_name);
        TextView textViewRepositoryDescription = view.findViewById(R.id.text_view_repository_description);
        TextView textViewUserName = view.findViewById(R.id.text_view_user_name);
        TextView textViewNumberOfForks = view.findViewById(R.id.text_view_number_of_forks);
        TextView textViewNumberOfStars = view.findViewById(R.id.text_view_number_of_stars);
        TextView textViewFirstNameAndLastName = view.findViewById(R.id.text_view_first_name_and_last_name);
        ImageView circleImageView = view.findViewById(R.id.profile_image);

        textViewRepositoryName.setText(item.getName());
        textViewRepositoryDescription.setText(item.getDescription());
        textViewNumberOfForks.setText(item.forksCount.toString());
        textViewNumberOfStars.setText(item.stargazersCount.toString());
        textViewUserName.setText(item.getOwner().getLogin());
        textViewFirstNameAndLastName.setText(item.getFullName());
        imageLoader.displayImage(item.getOwner().getAvatarUrl(), circleImageView);

        return view;

    }
}
