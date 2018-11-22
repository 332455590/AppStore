package piratehat.appstore.module;

import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import piratehat.appstore.Bean.AppBean;
import piratehat.appstore.Bean.BannerBean;
import piratehat.appstore.config.Constant;
import piratehat.appstore.config.Url;
import piratehat.appstore.contract.IMainContract;
import piratehat.appstore.diskCache.DiskCacheManager;
import piratehat.appstore.dto.AppsDataDto;
import piratehat.appstore.utils.GsonUtil;
import piratehat.appstore.utils.JsoupUtil;
import piratehat.appstore.utils.OkHttpResultCallback;
import piratehat.appstore.utils.OkHttpUtil;

/**
 *
 * Created by PirateHat on 2018/10/27.
 */

public class MainModule implements IMainContract.IModel {
    private static final String TAG = "MainModule";

    @Override
    public void getMainPage(final IMainContract.IPresenter presenter) {
//        String msg = null;
//       if ((msg=DiskCacheManager.getDiskInstance().getString(Url.MAIN_PAGE))!=null){
//           presenter.setBanner((ArrayList<BannerBean>) JsoupUtil.getInstance().getBanner(msg));
//           presenter.setRankApps((JsoupUtil.getInstance().getRankApps(msg)));
//           presenter.setBoutiqueApps(JsoupUtil.getInstance().getBoutiqueApps(msg));
//           return;
//       }
        Map<String, String> map = new HashMap<>();
        map.put(Constant.USER_AGENT, Constant.USER_AGENT_VALUE);
        OkHttpUtil.getInstance().getAsync(Url.MAIN_PAGE, new OkHttpResultCallback() {
            @Override
            public void onError(Call call, Exception e) {
                presenter.showError(e.getMessage());
            }

            @Override
            public void onResponse(String msg) {

               presenter.setBanner((ArrayList<BannerBean>) JsoupUtil.getInstance().getBanner(msg));
               presenter.setRankApps((JsoupUtil.getInstance().getRankApps(msg)));
               presenter.setBoutiqueApps(JsoupUtil.getInstance().getBoutiqueApps(msg));
//                DiskCacheManager.getDiskInstance().put(Url.MAIN_PAGE,msg);

            }

        }, map);
    }



}
