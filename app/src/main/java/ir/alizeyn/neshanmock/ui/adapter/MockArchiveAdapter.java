package ir.alizeyn.neshanmock.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Database;
import ir.alizeyn.neshanmock.R;
import ir.alizeyn.neshanmock.database.DatabaseClient;
import ir.alizeyn.neshanmock.database.MockEntity;
import ir.alizeyn.neshanmock.ui.activity.MockArchiveActivity;
import ir.alizeyn.neshanmock.ui.activity.PlayActivity;

/**
 * @author alizeyn
 * Created at 4/5/19
 */
public class MockArchiveAdapter extends RecyclerView.Adapter<MockArchiveAdapter.MockArchiveViewHolder> {

    private Context context;
    private List<MockEntity> mockList;

    public MockArchiveAdapter(Context context) {
        this.context = context;
        mockList = Collections.emptyList();
    }

    @NonNull
    @Override
    public MockArchiveViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View viewHolder = LayoutInflater.from(context).inflate(R.layout.item_mock_archive, parent, false);
        return new MockArchiveViewHolder(viewHolder);
    }

    @Override
    public void onBindViewHolder(@NonNull MockArchiveViewHolder holder, int position) {
        MockEntity mock = mockList.get(position);

        holder.tvType.setText(mock.getMockType());
        holder.tvDesc.setText(mock.getDesc());
        holder.tvDate.setText(DateFormat.format("yyyy/MM/dd  hh:mm:ss", mock.getId()));
    }

    @Override
    public int getItemCount() {
        return mockList.size();
    }

    public void setData(List<MockEntity> mocks) {
        this.mockList = mocks;
        notifyDataSetChanged();
    }

    class MockArchiveViewHolder extends RecyclerView.ViewHolder {

        TextView tvDate;
        TextView tvType;
        TextView tvDesc;

        ImageView ivMore;

        public MockArchiveViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tvTime);
            tvType = itemView.findViewById(R.id.tvType);
            tvDesc = itemView.findViewById(R.id.tvDesc);
            ivMore = itemView.findViewById(R.id.iv_more);

            ivMore.setOnClickListener(v -> {
                PopupMenu popupMenu = new PopupMenu(context, v);
                popupMenu.getMenuInflater().inflate(R.menu.menu_mock_item, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(menuItem -> {
                    MockEntity mock = mockList.get(getAdapterPosition());

                    switch (menuItem.getItemId()) {
                        case R.id.share:
                            Toast.makeText(context, "share", Toast.LENGTH_SHORT).show();
                            return true;
                        case R.id.upload:
                            Toast.makeText(context, "up", Toast.LENGTH_SHORT).show();
                            return true;
                        case R.id.del:
                            Toast.makeText(context, "del", Toast.LENGTH_SHORT).show();
                            int removePos = getAdapterPosition();
                            mockList.remove(removePos);
                            notifyItemRemoved(removePos);
                            AsyncTask.execute(() -> DatabaseClient.getInstance(context)
                                    .getMockDatabase()
                                    .getMockDao()
                                    .delete(mock));
                            return true;
                    }
                    return false;
                });
                popupMenu.show();
            });
            itemView.setOnClickListener(v -> {
                MockEntity mock = mockList.get(getAdapterPosition());
                Intent intent = new Intent(context, PlayActivity.class);
                intent.putExtra("mockData", mock);
                context.startActivity(intent);
            });
        }
    }
}