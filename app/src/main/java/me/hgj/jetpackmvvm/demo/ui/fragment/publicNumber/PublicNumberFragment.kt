package me.hgj.jetpackmvvm.demo.ui.fragment.publicNumber

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.kingja.loadsir.core.LoadService
import kotlinx.android.synthetic.main.include_viewpager.*
import me.hgj.jetpackmvvm.demo.R
import me.hgj.jetpackmvvm.demo.app.base.BaseFragment
import me.hgj.jetpackmvvm.demo.app.ext.*
import me.hgj.jetpackmvvm.demo.app.weight.loadCallBack.ErrorCallback
import me.hgj.jetpackmvvm.demo.data.model.bean.ClassifyResponse
import me.hgj.jetpackmvvm.demo.databinding.FragmentViewpagerBinding
import me.hgj.jetpackmvvm.demo.viewmodel.request.RequestPublicNumberViewModel
import me.hgj.jetpackmvvm.ext.parseState

/**
 * 作者　: hegaojian
 * 时间　: 2019/12/28
 * 描述　:聚合公众号
 */
class PublicNumberFragment :
    BaseFragment<RequestPublicNumberViewModel, FragmentViewpagerBinding>() {

    //界面状态管理者
    private lateinit var loadService: LoadService<Any>

    //fragment集合
    private var fragments: ArrayList<Fragment> = arrayListOf()

    //标题集合
    private var mDataList: ArrayList<ClassifyResponse> = arrayListOf()

    override fun layoutId() = R.layout.fragment_viewpager

    override fun initView(savedInstanceState: Bundle?) {
        //状态页配置
        loadService = loadServiceInit(view_pager) {
            //点击重试时触发的操作
            loadService.showLoading()
            mViewModel.getPublicTitleData()
        }
        //初始化viewpager2
        view_pager.init(this, fragments)
        //初始化 magic_indicator
        magic_indicator.bindViewPager2(view_pager, mDataList)
        appViewModel.appColor.value?.let { setUiTheme(it, viewpager_linear, loadService) }
    }

    /**
     * 懒加载
     */
    override fun lazyLoadData() {
        //设置界面 加载中
        loadService.showLoading()
        //请求标题数据
        mViewModel.getPublicTitleData()
    }

    override fun createObserver() {
        mViewModel.titleData.observe(viewLifecycleOwner, { data ->
            parseState(data, {
                mDataList.addAll(it)
                it.forEach { classify ->
                    fragments.add(PublicChildFragment.newInstance(classify.id))
                }
                magic_indicator.navigator.notifyDataSetChanged()
                view_pager.adapter?.notifyDataSetChanged()
                view_pager.offscreenPageLimit = fragments.size
                loadService.showSuccess()
            }, {
                //请求项目标题失败
                loadService.showCallback(ErrorCallback::class.java)
                loadService.setErrorText(it.errorMsg)
            })
        })
        appViewModel.appColor.observe(viewLifecycleOwner, {
            setUiTheme(it, viewpager_linear, loadService)
        })
    }
}