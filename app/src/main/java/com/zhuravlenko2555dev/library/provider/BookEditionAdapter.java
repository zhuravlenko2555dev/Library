package com.zhuravlenko2555dev.library.provider;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zhuravlenko2555dev.library.R;
import com.zhuravlenko2555dev.library.activity.BookActivity;
import com.zhuravlenko2555dev.library.activity.SelectedBookActivity;
import com.zhuravlenko2555dev.library.activity.SelectedBookEditionActivity;

import java.util.ArrayList;

public class BookEditionAdapter extends RecyclerView.Adapter<BookEditionAdapter.ViewHolder> {
    private Context context;
    private ArrayList<BookEditionItem> bookEditionItemArrayList;
    private String queryOfBook = "";
    private String keyOfBook = "";

    public BookEditionAdapter(Context context, ArrayList<BookEditionItem> bookEditionItemArrayList, String queryOfBook, String keyOfBook) {
        this.context = context;
        this.bookEditionItemArrayList = bookEditionItemArrayList;
        this.queryOfBook = queryOfBook;
        this.keyOfBook = keyOfBook;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_book_edition, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.getTextViewBookEditionLanguage().setText(bookEditionItemArrayList.get(position).getLanguage());
        holder.getTextViewBookEditionPublisher().setText(bookEditionItemArrayList.get(position).getPublisher());
    }

    @Override
    public int getItemCount() {
        return bookEditionItemArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewBookEditionLanguage, textViewBookEditionPublisher;

        public ViewHolder(final View itemView) {
            super(itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intentSelectedBookEdition = new Intent(context, SelectedBookEditionActivity.class);
                    intentSelectedBookEdition.putExtra(BookActivity.EXTRA_QUERY_OF_BOOK, queryOfBook);
                    intentSelectedBookEdition.putExtra(SelectedBookActivity.EXTRA_KEY_OF_BOOK, keyOfBook);
                    intentSelectedBookEdition.putExtra(SelectedBookEditionActivity.EXTRA_LANGUAGE, bookEditionItemArrayList.get(getAdapterPosition()).getLanguage());
                    intentSelectedBookEdition.putExtra(SelectedBookEditionActivity.EXTRA_PUBLISHER, bookEditionItemArrayList.get(getAdapterPosition()).getPublisher());
                    context.startActivity(intentSelectedBookEdition);
                }
            });

            textViewBookEditionLanguage = itemView.findViewById(R.id.textViewBookEditionLanguage);
            textViewBookEditionPublisher = itemView.findViewById(R.id.textViewBookEditionPublisher);
        }

        public TextView getTextViewBookEditionLanguage() {
            return textViewBookEditionLanguage;
        }

        public TextView getTextViewBookEditionPublisher() {
            return textViewBookEditionPublisher;
        }
    }
}
