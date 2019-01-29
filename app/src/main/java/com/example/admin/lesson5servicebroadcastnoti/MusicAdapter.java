package com.example.admin.lesson5servicebroadcastnoti;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.ViewHolder> {
    private List<Music> mMusics;
    private static OnItemClickListener mListener;

    public MusicAdapter(List<Music> musics, OnItemClickListener listener) {
        mMusics = musics;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.setData(mMusics.get(position), mListener);
    }

    @Override
    public int getItemCount() {
        return mMusics.size() > 0 ? mMusics.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView mTextArtist;
        private TextView mTextSong;

        public ViewHolder(final View itemView) {
            super(itemView);
            mTextArtist = itemView.findViewById(R.id.text_name_artist);
            mTextSong = itemView.findViewById(R.id.text_name_song);
            itemView.setOnClickListener(this);
        }

        public void setData(final Music music, final OnItemClickListener listener) {
            mTextArtist.setText(music.getArtist());
            mTextSong.setText(music.getName());
        }

        @Override
        public void onClick(View view) {
            mListener.onItemClick(view, this.getLayoutPosition());
        }
    }
}
