package com.akshathjain.bookworm.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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

public class SearchResult extends AppCompatActivity {
    private RecyclerView resultView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent args = getIntent();

        getSupportActionBar().setTitle(args.getStringExtra("query"));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        resultView = findViewById(R.id.search_result_view);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
        resultView.setLayoutManager(layoutManager);

        //query database and get search results
        LibrivoxRetriever retriever = new LibrivoxRetriever();
        retriever.addOnCompleted(new QueryFinished<ArrayList<AudioBook>>() {
            @Override
            public void onQueryFinished(ArrayList<AudioBook> o) {
                GridAdapter mAdapter = new GridAdapter(SearchResult.this, o);
                resultView.setAdapter(mAdapter);
            }
        });
        retriever.execute(generateSearchString(args.getStringExtra("query")));

    }

    private String generateSearchString(String s){
        String begin = "https://librivox.org/api/feed/audiobooks/?title=^";
        String end = "&format=json&extended=1";
        String[] split = s.split(" ");
        String mid = split[0];
        for(int i = 1; i < split.length; i++)
            mid += "%20" + split[i];
        return begin + mid + end;
    }

    class GridAdapter extends RecyclerView.Adapter<GridAdapter.ViewHolder>{
        private ArrayList<AudioBook> list;
        private Context context;

        public GridAdapter(Context c, ArrayList<AudioBook> list){
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

        class ViewHolder extends RecyclerView.ViewHolder{
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
