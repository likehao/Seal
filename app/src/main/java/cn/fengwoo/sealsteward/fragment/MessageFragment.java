package cn.fengwoo.sealsteward.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.fengwoo.sealsteward.R;
import cn.fengwoo.sealsteward.activity.WaitMeAgreeActivity;

/**
 * 消息
 */
public class MessageFragment extends Fragment implements View.OnClickListener{
    private View view;
    @BindView(R.id.wait_me_agree_ll)
    LinearLayout wait_me_agree_ll;
    private Intent intent;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.message_fragment,container,false);

        ButterKnife.bind(this,view);
        setListener();
        return view;
    }

    private void setListener() {
        wait_me_agree_ll.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.wait_me_agree_ll:
                 intent = new Intent(getActivity(), WaitMeAgreeActivity.class);
                 startActivity(intent);
                 break;
        }
    }
}
