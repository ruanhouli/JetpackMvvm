package me.hgj.jetpackmvvm.demo.ui.adapter

import android.text.TextUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.chad.library.adapter.base.BaseDelegateMultiAdapter
import com.chad.library.adapter.base.delegate.BaseMultiTypeDelegate
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import me.hgj.jetpackmvvm.demo.R
import me.hgj.jetpackmvvm.demo.app.ext.setAdapterAnimation
import me.hgj.jetpackmvvm.demo.app.util.SettingUtil
import me.hgj.jetpackmvvm.demo.app.weight.customview.CollectView
import me.hgj.jetpackmvvm.demo.data.model.bean.ArticleResponse
import me.hgj.jetpackmvvm.ext.util.toHtml


class ArticleAdapter(data: MutableList<ArticleResponse>?) :
    BaseDelegateMultiAdapter<ArticleResponse, BaseViewHolder>(data) {
    private val article = 1//文章类型
    private val project = 2//项目类型 本来打算不区分文章和项目布局用统一布局的，但是布局完以后发现差异化蛮大的，所以还是分开吧
    private var showTag = false//是否展示标签 tag 一般主页才用的到

    private var collectAction: (item: ArticleResponse, v: CollectView, position: Int) -> Unit =
        { _: ArticleResponse, _: CollectView, _: Int -> }

    constructor(data: MutableList<ArticleResponse>?, showTag: Boolean) : this(data) {
        this.showTag = showTag
    }

    init {
        setAdapterAnimation(SettingUtil.getListMode())
        // 第一步，设置代理
        setMultiTypeDelegate(object : BaseMultiTypeDelegate<ArticleResponse>() {
            override fun getItemType(data: List<ArticleResponse>, position: Int): Int {
                //根据是否有图片 判断为文章还是项目，好像有点low的感觉。。。我看实体类好像没有相关的字段，就用了这个，也有可能是我没发现
                return if (TextUtils.isEmpty(data[position].envelopePic)) article else project
            }
        })
        // 第二步，绑定 item 类型
        getMultiTypeDelegate()?.let {
            it.addItemType(article, R.layout.item_ariticle)
            it.addItemType(project, R.layout.item_project)
        }
    }

    override fun convert(holder: BaseViewHolder, item: ArticleResponse) {
        when (holder.itemViewType) {
            article -> {
                //文章布局的赋值
                item.run {
                    holder.setText(
                        R.id.item_home_author,
                        if (author.isNotEmpty()) author else shareUser
                    )
                    holder.setText(R.id.item_home_content, title.toHtml())
                    holder.setText(R.id.item_home_type2, "$superChapterName·$chapterName".toHtml())
                    holder.setText(R.id.item_home_date, niceDate)
                    holder.getView<CollectView>(R.id.item_home_collect).isChecked = collect
                    if (showTag) {
                        //展示标签
                        holder.setGone(R.id.item_home_new, !fresh)
                        holder.setGone(R.id.item_home_top, type != 1)
                        if (tags.isNotEmpty()) {
                            holder.setGone(R.id.item_home_type1, false)
                            holder.setText(R.id.item_home_type1, tags[0].name)
                        } else {
                            holder.setGone(R.id.item_home_type1, true)
                        }
                    } else {
                        //隐藏所有标签
                        holder.setGone(R.id.item_home_top, true)
                        holder.setGone(R.id.item_home_type1, true)
                        holder.setGone(R.id.item_home_new, true)
                    }
                }
                holder.getView<CollectView>(R.id.item_home_collect)
                    .setOnCollectViewClickListener(object : CollectView.OnCollectViewClickListener {
                        override fun onClick(v: CollectView) {
                            collectAction.invoke(item, v, holder.adapterPosition)
                        }
                    })
            }
            project -> {
                //项目布局的赋值
                item.run {
                    holder.setText(
                        R.id.item_project_author,
                        if (author.isNotEmpty()) author else shareUser
                    )
                    holder.setText(R.id.item_project_title, title.toHtml())
                    holder.setText(R.id.item_project_content, desc.toHtml())
                    holder.setText(
                        R.id.item_project_type,
                        "$superChapterName·$chapterName".toHtml()
                    )
                    holder.setText(R.id.item_project_date, niceDate)
                    if (showTag) {
                        //展示标签
                        holder.setGone(R.id.item_project_new, !fresh)
                        holder.setGone(R.id.item_project_top, type != 1)
                        if (tags.isNotEmpty()) {
                            holder.setGone(R.id.item_project_type1, false)
                            holder.setText(R.id.item_project_type1, tags[0].name)
                        } else {
                            holder.setGone(R.id.item_project_type1, true)
                        }
                    } else {
                        //隐藏所有标签
                        holder.setGone(R.id.item_project_top, true)
                        holder.setGone(R.id.item_project_type1, true)
                        holder.setGone(R.id.item_project_new, true)
                    }
                    holder.getView<CollectView>(R.id.item_project_collect).isChecked = collect
                    Glide.with(context).load(envelopePic)
                        .transition(DrawableTransitionOptions.withCrossFade(500))
                        .into(holder.getView(R.id.item_project_imageview))
                }
                holder.getView<CollectView>(R.id.item_project_collect)
                    .setOnCollectViewClickListener(object : CollectView.OnCollectViewClickListener {
                        override fun onClick(v: CollectView) {
                            collectAction.invoke(item, v, holder.adapterPosition)
                        }
                    })
            }
        }
    }

    fun setCollectClick(inputCollectAction: (item: ArticleResponse, v: CollectView, position: Int) -> Unit) {
        this.collectAction = inputCollectAction
    }

}


