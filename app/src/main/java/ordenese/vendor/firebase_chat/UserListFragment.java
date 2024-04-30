package ordenese.vendor.firebase_chat;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

import ordenese.vendor.R;
import ordenese.vendor.common.AppLanguageSupport;
import ordenese.vendor.common.Constant;

public class UserListFragment extends Fragment {

    View mView;
    FirebaseAuth auth;
    DatabaseReference reference;
    ArrayList<Users> usersArrayList = new ArrayList<>();
    RecyclerView users_list;
    Activity activity;
    UsersAdapter adapter;
    ProgressBar progress_bar;
    String vendor_uid = "";
    TextView tv_empty;

    public UserListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(AppLanguageSupport.onAttach(context));
        this.activity = (Activity) context;
        if (getActivity() != null) {
            getActivity().getWindow().getDecorView().setLayoutDirection(
                    "ae".equals(AppLanguageSupport.getLanguage(getActivity())) ?
                            View.LAYOUT_DIRECTION_RTL : View.LAYOUT_DIRECTION_LTR);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_user_list, container, false);

        auth = FirebaseAuth.getInstance();
        vendor_uid = Constant.DataGetValue(activity, "vendor_uid");

        setHasOptionsMenu(true);
        tv_empty = mView.findViewById(R.id.tv_empty);
        users_list = mView.findViewById(R.id.users_list);
        progress_bar = mView.findViewById(R.id.progress_bar);
        reference = FirebaseDatabase.getInstance().getReference("messages").child(vendor_uid);
        users_list.setLayoutManager(new LinearLayoutManager(activity, RecyclerView.VERTICAL, false));
        adapter = new UsersAdapter(activity, usersArrayList);
        users_list.setAdapter(adapter);

        reference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                usersArrayList.clear();
                if (snapshot.getChildrenCount() != 0) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        if (dataSnapshot.getKey() != null) {
                            reference = FirebaseDatabase.getInstance().getReference("users_list").child(dataSnapshot.getKey());
                            reference.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.getValue() != null) {
                                        if (!Objects.equals(dataSnapshot.getKey(), vendor_uid)) {
                                            Users users = snapshot.getValue(Users.class);
                                            if (users != null && users.getUid() != null) {
                                                usersArrayList.add(users);
                                                adapter = new UsersAdapter(activity, usersArrayList);
                                                users_list.setAdapter(adapter);
                                                adapter.notifyDataSetChanged();
                                            }
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Constant.showToast(activity.getResources().getString(R.string.process_failed_please_try_again));
                                    progress_bar.setVisibility(View.GONE);
                                }
                            });
                        }
                    }
                }
                progress_bar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Constant.showToast(activity.getResources().getString(R.string.process_failed_please_try_again));
                progress_bar.setVisibility(View.GONE);
            }
        });

        if (vendor_uid.isEmpty()){
            tv_empty.setVisibility(View.VISIBLE);
            users_list.setVisibility(View.GONE);
        }else {
            tv_empty.setVisibility(View.GONE);
            users_list.setVisibility(View.VISIBLE);
        }

        return mView;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_item) {
//            Fragment fragment = new AllUsersFragment();
//            FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
//            fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
//                    android.R.anim.fade_out);
//            fragmentTransaction.replace(R.id.home_container_without_tb, fragment, "AllUsersFragment").addToBackStack("AllUsersFragment");
//            fragmentTransaction.commitAllowingStateLoss();

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (adapter != null) {
            adapter = new UsersAdapter(activity, usersArrayList);
            users_list.setAdapter(adapter);
        }
        progress_bar.setVisibility(View.GONE);
    }

    class UsersAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        ArrayList<Users> users;
        Context mContext;

        UsersAdapter(Context context, ArrayList<Users> users) {
            this.mContext = context;
            this.users = users;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.users_rec_list, parent, false);
            return new ChatViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
            ChatViewHolder chatViewHolder = (ChatViewHolder) holder;
            chatViewHolder.mCustomerName.setText(users.get(position).getUser_name());
            if (users.get(position).getUid() != null) {
                chatViewHolder.user_linear.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FirebaseChat firebaseChat = new FirebaseChat();
                        Bundle bundle = new Bundle();
                        bundle.putString("user_id", users.get(position).getUid());
                        bundle.putString("user_name", users.get(position).getUser_name());
                        firebaseChat.setArguments(bundle);
                        getParentFragmentManager().beginTransaction()
                                .replace(R.id.home_container_without_tb, firebaseChat, "LoadChatList")
                                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                                .addToBackStack("LoadChatList")
                                .commitAllowingStateLoss();
                    }
                });
            }

            if (users.get(position).getUid() != null) {
                DatabaseReference referenceChat = FirebaseDatabase.getInstance().getReference("messages").child(Constant.DataGetValue(activity, "vendor_uid")).child(users.get(position).getUid());
                referenceChat.addValueEventListener(new ValueEventListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        int count = 0;
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            if (snapshot.getValue() != null) {
                                Chat chat = snapshot.getValue(Chat.class);
                                if (chat != null) {
//                                    if (!chat.getSender().equals(Constant.DataGetValue(activity, "vendor_uid"))) {
                                    if (chat.getSeen() != null) {
                                        if (chat.getSeen().equals("false")) {
                                            count = count + 1;
                                        }
                                    }
//                                    }
                                }
                                if (count == 0) {
                                    chatViewHolder.text_count.setVisibility(View.GONE);
                                } else {
                                    chatViewHolder.text_count.setVisibility(View.VISIBLE);
                                    chatViewHolder.text_count.setText(String.valueOf(count));
                                }

                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

        }

        @Override
        public long getItemId(int position) {
            return super.getItemId(position);
        }

        @Override
        public int getItemCount() {
            return users.size();
        }


        public class ChatViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            TextView mCustomerName, text_count;
            FrameLayout user_linear;

            public ChatViewHolder(View itemView) {
                super(itemView);
                mCustomerName = itemView.findViewById(R.id.user_name);
                text_count = itemView.findViewById(R.id.text_count);
                user_linear = itemView.findViewById(R.id.user_linear);
            }

            @Override
            public void onClick(View v) {

            }
        }

    }

}