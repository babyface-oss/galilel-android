package galilel.org.galilelwallet.ui.settings_network_activity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.content.ContextCompat;

import org.galilelj.core.Peer;

import java.util.List;

import galilel.org.galilelwallet.R;
import galilel.org.galilelwallet.ui.base.BaseRecyclerFragment;
import galilel.org.galilelwallet.ui.base.tools.adapter.BaseRecyclerAdapter;

public class NetworkFragment extends BaseRecyclerFragment<Peer> {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        setEmptyText(getString(R.string.empty_connection));
        setEmptyTextColor(ContextCompat.getColor(getContext(), R.color.grey85black));
        return view;
    }

    @Override
    protected List<Peer> onLoading() {
        return galilelModule.listConnectedPeers();
    }

    @Override
    protected BaseRecyclerAdapter<Peer, ? extends NetworkViewHolder> initAdapter() {
        return new BaseRecyclerAdapter<Peer, NetworkViewHolder>(getActivity()) {
            @Override
            protected NetworkViewHolder createHolder(View itemView, int type) {
                return new NetworkViewHolder(itemView);
            }

            @Override
            protected int getCardViewResource(int type) {
                return R.layout.network_row;
            }

            @Override
            protected void bindHolder(NetworkViewHolder holder, Peer data, int position) {
                holder.address.setText(data.getAddress().toString());
                holder.network_ip.setText(data.getPeerVersionMessage().subVer);
                holder.protocol.setText(getString(R.string.node_protocol) + " " + data.getPeerVersionMessage().clientVersion);
                holder.blocks.setText(data.getBestHeight() + " " + getString(R.string.node_block));
                holder.speed.setText(data.getLastPingTime() + getString(R.string.node_millisecond));
            }
        };
    }
}
