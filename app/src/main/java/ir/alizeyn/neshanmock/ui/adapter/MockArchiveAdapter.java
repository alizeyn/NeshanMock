package ir.alizeyn.neshanmock.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Collections;
import java.util.List;

import ir.alizeyn.neshanmock.R;
import ir.alizeyn.neshanmock.database.DatabaseClient;
import ir.alizeyn.neshanmock.database.MockEntity;
import ir.alizeyn.neshanmock.database.PosEntity;
import ir.alizeyn.neshanmock.mock.MockShareModel;
import ir.alizeyn.neshanmock.mock.MockType;
import ir.alizeyn.neshanmock.request.MockWebServices;
import ir.alizeyn.neshanmock.request.RequestFactory;
import ir.alizeyn.neshanmock.ui.activity.PlayCustomActivity;
import ir.alizeyn.neshanmock.ui.activity.PlayTrackActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * @author alizeyn
 * Created at 4/5/19
 */
public class MockArchiveAdapter extends RecyclerView.Adapter<MockArchiveAdapter.MockArchiveViewHolder> {

    private Context context;
    private List<MockEntity> mockList;

    private MockWebServices webServices;

    public MockArchiveAdapter(Context context) {
        this.context = context;
        mockList = Collections.emptyList();
        webServices = RequestFactory.mockWebServices();
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
                        case R.id.save:
                            AsyncTask.execute(() -> {
                                try {
                                    List<PosEntity> poses = DatabaseClient.getInstance(context)
                                            .getMockDatabase()
                                            .getPosDao()
                                            .getMockPos(mock.getId());
                                    MockShareModel shareModel = new MockShareModel(mock, poses);
                                    File file = new File(Environment.getExternalStoragePublicDirectory("NeshanMock"), mock.getId() + ".mock");
                                    if (!file.exists()) {
                                        file.getParentFile().mkdirs();
                                        file.createNewFile();
                                    }
                                    ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file));
                                    out.writeObject(shareModel);
                                    out.close();

                                    new Handler(Looper.getMainLooper()).post(() -> {
                                        Toast.makeText(context, "Saved", Toast.LENGTH_SHORT).show();
                                    });

                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            });

                            return true;
                        case R.id.upload:
                            Toast.makeText(context, "up", Toast.LENGTH_SHORT).show();

                            AsyncTask.execute(() -> {

                                List<PosEntity> poses = DatabaseClient.getInstance(context)
                                        .getMockDatabase()
                                        .getPosDao()
                                        .getMockPos(mock.getId());
                                MockShareModel shareModel = new MockShareModel(mock, poses);

                                Call<Void> call = webServices.saveMock(shareModel);
                                call.enqueue(new Callback<Void>() {
                                    @Override
                                    public void onResponse(Call<Void> call, Response<Void> response) {

                                        if (response.isSuccessful()) {
                                            new Handler(Looper.getMainLooper()).post(() -> {
                                                Toast.makeText(context, "Uploaded", Toast.LENGTH_SHORT).show();
                                            });
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<Void> call, Throwable t) {

                                    }
                                });
                            });
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
                Intent intent;
                if (mock.getMockType().equals(MockType.TRACK.name())) {
                    intent = new Intent(context, PlayTrackActivity.class);
                } else {
                    intent = new Intent(context, PlayCustomActivity.class);
                }
                intent.putExtra("mockData", mock);
                context.startActivity(intent);
            });
        }
    }
}
