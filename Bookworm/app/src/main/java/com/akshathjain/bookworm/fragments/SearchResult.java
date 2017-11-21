package com.akshathjain.bookworm.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.akshathjain.bookworm.R;
import com.akshathjain.bookworm.async.LibrivoxRetriever;
import com.akshathjain.bookworm.generic.AudioBook;
import com.akshathjain.bookworm.interfaces.QueryFinished;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class SearchResult extends Fragment {
    private RecyclerView resultView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_search_result, container, false);

        Toolbar toolbar = layout.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        Bundle args = getArguments();

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(args.getString("query"));
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        resultView = layout.findViewById(R.id.search_result_view);
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 3);
        resultView.setLayoutManager(layoutManager);

        //query database and get search results
        LibrivoxRetriever retriever = new LibrivoxRetriever();
        retriever.addOnCompleted(new QueryFinished<ArrayList<AudioBook>>() {
            @Override
            public void onQueryFinished(ArrayList<AudioBook> o) {
                if (o.size() > 0) {
                    GridAdapter mAdapter = new GridAdapter(getActivity(), o);
                    resultView.addItemDecoration(new CustomItemDecoration(getResources().getDimensionPixelSize(R.dimen.book_spacing)));
                    resultView.setAdapter(mAdapter);
                }
            }
        });
        retriever.execute(generateSearchString(args.getString("query")));

        return layout;
    }

    private String generateSearchString(String s) {
        String begin = "https://librivox.org/api/feed/audiobooks/?title=^";
        String end = "&format=json&extended=1";
        String[] split = s.split(" ");
        String mid = split[0];
        for (int i = 1; i < split.length; i++)
            mid += "%20" + split[i];
        return begin + mid + end;
    }

    private OnBackPressed backCallback;
    interface OnBackPressed{
        void backPressed();
    }
    public void addOnBackPressedListener(OnBackPressed callback){
    }

    class GridAdapter extends RecyclerView.Adapter<GridAdapter.ViewHolder> {
        private ArrayList<AudioBook> list;
        private Context context;

        public GridAdapter(Context c, ArrayList<AudioBook> list) {
            this.context = c;
            this.list = list;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.book, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int i) {
            Glide.with(context).load(list.get(i).getThumbnailURL()).into(holder.thumbnailView);
            holder.titleView.setText(list.get(i).getTitle());
            holder.authorView.setText(list.get(i).getAuthor());
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            private ImageView thumbnailView;
            private TextView titleView;
            private TextView authorView;

            public ViewHolder(View itemView) {
                super(itemView);
                thumbnailView = itemView.findViewById(R.id.book_thumbnail);
                titleView = itemView.findViewById(R.id.book_title);
                authorView = itemView.findViewById(R.id.book_author);
            }
        }
    }
}

//custom class that deals with recycler view spacing
class CustomItemDecoration extends RecyclerView.ItemDecoration {
    private int space;

    public CustomItemDecoration(int space) {
        this.space = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        GridLayoutManager manager = (GridLayoutManager) parent.getLayoutManager();
        int span = manager.getSpanCount();
        int pos = parent.getChildLayoutPosition(view) % span;

        outRect.top = parent.getChildLayoutPosition(view) < span ? (int) (space * 1.5) : 0;
        outRect.left = pos == 0 ? space : space / 2;
        outRect.right = pos == span - 1 ? space : space / 2;
        outRect.bottom = (int) (space * 1.5);
    }
}