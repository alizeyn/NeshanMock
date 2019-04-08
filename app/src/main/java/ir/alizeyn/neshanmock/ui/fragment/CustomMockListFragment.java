package ir.alizeyn.neshanmock.ui.fragment;


import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ir.alizeyn.neshanmock.R;
import ir.alizeyn.neshanmock.database.DatabaseClient;
import ir.alizeyn.neshanmock.database.MockEntity;
import ir.alizeyn.neshanmock.ui.adapter.MockArchiveAdapter;

public class CustomMockListFragment extends Fragment {


    private RecyclerView rlMockArchive;
    private MockArchiveAdapter adapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup parent = (ViewGroup) inflater.inflate(R.layout.fragment_custom_mock_list, container, false);
        adapter = new MockArchiveAdapter(getActivity());
        rlMockArchive = parent.findViewById(R.id.rlMockArchive);
        rlMockArchive.setLayoutManager(new LinearLayoutManager(getActivity()));
        rlMockArchive.setAdapter(adapter);
        new showMockArchiveTask().execute();
        return parent;
    }

    class showMockArchiveTask extends AsyncTask<Void, Void, List<MockEntity>> {

        @Override
        protected List<MockEntity> doInBackground(Void... voids) {
            return DatabaseClient.getInstance(getActivity())
                    .getMockDatabase()
                    .getMockDao()
                    .getCustoms();
        }

        @Override
        protected void onPostExecute(List<MockEntity> mockEntities) {
            adapter.setData(mockEntities);
        }
    }
}
