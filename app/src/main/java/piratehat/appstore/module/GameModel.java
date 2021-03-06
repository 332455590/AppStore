package piratehat.appstore.module;

import android.util.Log;

import com.shizhefei.mvc.RequestHandle;
import com.shizhefei.mvc.ResponseSender;

import java.util.ArrayList;
import java.util.HashMap;

import java.util.List;
import java.util.Map;

import okhttp3.Call;
import piratehat.appstore.Bean.AppBean;
import piratehat.appstore.config.Constant;
import piratehat.appstore.config.Url;
import piratehat.appstore.contract.IGameContract;

import piratehat.appstore.dto.AppsDataDto;


import piratehat.appstore.utils.CacheUtil;
import piratehat.appstore.utils.GsonUtil;
import piratehat.appstore.utils.OkHttpResultCallback;
import piratehat.appstore.utils.OkHttpUtil;

/**
 *
 * Created by PirateHat on 2018/11/13.
 */

public class GameModel implements IGameContract.IModel {
    private boolean mHasMore;
    private int mPageContext;
    private static final String TAG = "GameModel";

    @Override
    public void getAllApps(final IGameContract.IPresenter presenter) {

        List list ;
        if ((list = CacheUtil.getInstance().get(Url.GAME_ALL + mPageContext))!=null){
            presenter.setAppsList((ArrayList<AppBean>) list);
            return ;
        }

        Map<String, String> map = new HashMap<>();
        map.put(Constant.USER_AGENT, Constant.USER_AGENT_VALUE);
        OkHttpUtil.getInstance().getAsync(Url.GAME_ALL + mPageContext, new OkHttpResultCallback() {
            @Override
            public void onError(Call call, Exception e) {

                presenter.showError(e.getMessage());
            }

            @Override
            public void onResponse(String msg) {
                ArrayList<AppBean> beans = new ArrayList<>();
                try {
                    beans = (ArrayList<AppBean>) GsonUtil.gsonToBean(msg, AppsDataDto.class).transform();

                } catch (IllegalStateException e) {

                } finally {
                    mHasMore = beans.size() != 0;
                    presenter.setAppsList(beans);
                    CacheUtil.getInstance().put(Url.GAME_ALL + mPageContext,beans);
                }
            }
        }, map);
    }

    @Override
    public RequestHandle refresh(ResponseSender<ArrayList<AppBean>> sender) throws Exception {

        return new OkHttpRequestHandle();
    }

    @Override
    public RequestHandle loadMore(ResponseSender<ArrayList<AppBean>> sender) throws Exception {
        mPageContext += 20;
        return loadApps(sender, mPageContext);
    }

    @Override
    public boolean hasMore() {

        return mHasMore;
    }

    private RequestHandle loadApps(final ResponseSender<ArrayList<AppBean>> sender, final int pageContext) {
        List list ;
        if ((list = CacheUtil.getInstance().get(Url.GAME_ALL + mPageContext))!=null){
            sender.sendData((ArrayList<AppBean>) list);
            return new OkHttpRequestHandle();
        }


        Map<String, String> map = new HashMap<>();
        map.put(Constant.USER_AGENT, Constant.USER_AGENT_VALUE);
        OkHttpUtil.getInstance().getAsync(Url.GAME_ALL + pageContext, new OkHttpResultCallback() {
            @Override
            public void onError(Call call, Exception e) {
                sender.sendError(e);
            }

            @Override
            public void onResponse(String msg) {

                ArrayList<AppBean> beans = (ArrayList<AppBean>) GsonUtil.gsonToBean(msg, AppsDataDto.class).transform();
                mHasMore = beans.size() != 0;
                sender.sendData(beans);
                CacheUtil.getInstance().put(Url.GAME_ALL + pageContext,beans);
            }
        }, map);
        return new OkHttpRequestHandle();
    }
}
