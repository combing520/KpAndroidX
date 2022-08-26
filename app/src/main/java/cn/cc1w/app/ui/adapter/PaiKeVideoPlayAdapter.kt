package cn.cc1w.app.ui.adapter

import android.app.Activity
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import app.cloud.ccwb.cn.linlibrary.image.RoundAngleImageView
import cn.cc1w.app.ui.R
import cn.cc1w.app.ui.constants.Constant
import cn.cc1w.app.ui.entity.ItemVideoListEntity
import cn.cc1w.app.ui.entity.VideoAndPicPriseEntity
import cn.cc1w.app.ui.entity.entity_public_use_js.ShareEntity
import cn.cc1w.app.ui.ui.home.record.PromotionDetailActivity
import cn.cc1w.app.ui.ui.home.record.UserPaikewActivity
import cn.cc1w.app.ui.ui.share.ShareActivity
import cn.cc1w.app.ui.ui.usercenter.setting.UpdateMobileActivity
import cn.cc1w.app.ui.ui.usercenter.usercenter.login.LoginActivity
import cn.cc1w.app.ui.utils.*
import cn.ccwb.cloud.httplibrary.rxhttp.entity.EventMessage
import cn.ccwb.cloud.httplibrary.rxhttp.rxhttp.error.ErrorInfo
import cn.ccwb.cloud.httplibrary.rxhttp.rxhttp.error.OnError
import cn.ccwb.cloud.httplibrary.rxhttp.rxhttp.exception.AuthException
import com.google.gson.Gson
import com.rxjava.rxlife.lifeOnMain
import org.greenrobot.eventbus.EventBus
import rxhttp.RxHttp

/**
 * 拍客视频播放
 */
class PaiKeVideoPlayAdapter(val context: Activity, var owner: LifecycleOwner) :
    RecyclerView.Adapter<PaiKeVideoPlayAdapter.ViewHolder>() {
    private var mVideoBeans: List<ItemVideoListEntity.DataBean> = ArrayList()
    private var lastClickTime: Long = System.currentTimeMillis()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_paike_video_recycle, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mVideoBeans[position]
        holder.authorUserNameTv.text = item.nickname
        holder.worksDescribeTv.text = item.title
        var praiseSelect = item.praise_select
        holder.priseCntTv.text = item.praise_number.toString()
        holder.commentCntTv.text = item.comment_number.toString()
        AppUtil.loadAvatarImg(item.head_pic_path, holder.authorAvatar)
        // 是否推广（1-是，0-否
        if (item.is_promotion == 1) {
            holder.promotionLayout.visibility = View.VISIBLE
            holder.promotionTv.text =
                if (TextUtils.isEmpty(item.promotion_name)) "" else item.promotion_name
        } else {
            holder.promotionLayout.visibility = View.GONE
            holder.promotionTv.text = ""
        }
        if (praiseSelect == STATUS_PRISE_UN_SELECT) {
            AppUtil.loadRes(R.mipmap.ic_like_paikew, holder.priseImg)
        } else {
            AppUtil.loadRes(R.mipmap.ic_like_paikew_select, holder.priseImg)
        }
        holder.priseBtn.setOnClickListener {
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastClickTime >= Constant.MIN_TIME_INTERVAL) {
                if (TextUtils.isEmpty(Constant.CW_AUTHORIZATION)) {
                    val bundle = Bundle()
                    bundle.putString("from", Constant.PHONE_BIND)
                    IntentUtil.startActivity(context, LoginActivity::class.java, bundle)
                    return@setOnClickListener
                }
                val userInfo = SharedPreferenceUtil.getUserInfo()
                if (null != userInfo) {
                    if (TextUtils.isEmpty(userInfo.mobile)) {
                        val bundle = Bundle()
                        bundle.putString("from", Constant.PHONE_BIND)
                        IntentUtil.startActivity(context, UpdateMobileActivity::class.java, bundle)
                        return@setOnClickListener
                    }
                }
                if (NetUtil.isNetworkConnected(context)) {
                    val status =
                        if (praiseSelect == STATUS_PRISE_SELECT) STATUS_PRISE_UN_SELECT else STATUS_PRISE_SELECT
                    LogUtil.e("status = $status")
                    RxHttp.postForm(Constant.PRISE_PAIKEW).add("shoot_id", item.id)
                        .add("status", status)
                        .asResponse(VideoAndPicPriseEntity.DataBean::class.java)
                        .lifeOnMain(owner)
                        .subscribe({ data: VideoAndPicPriseEntity.DataBean? ->
                            if (data != null) {
                                if (praiseSelect == STATUS_PRISE_SELECT) { // 当前是被赞状态； 需要进行取消点赞
                                    praiseSelect = STATUS_PRISE_UN_SELECT
                                    AppUtil.loadRes(R.mipmap.ic_like_paikew, holder.priseImg)
                                    mVideoBeans[position].praise_number =
                                        mVideoBeans[position].praise_number - 1
                                } else { // 当前处于未被点赞状态； 需要去进行点赞
                                    praiseSelect = STATUS_PRISE_SELECT
                                    AppUtil.loadRes(R.mipmap.ic_like_paikew_select, holder.priseImg)
                                    mVideoBeans[position].praise_number =
                                        mVideoBeans[position].praise_number + 1
                                }
                                mVideoBeans[position].praise_select = praiseSelect
                                holder.priseCntTv.text = data.praise_number.toString()
                            }
                        }, OnError { error: ErrorInfo ->
                            ToastUtil.showShortToast(error.errorMsg)
                            if (error.throwable != null && error.throwable is AuthException) {
                                AppUtil.doUserLogOut()
                                IntentUtil.startActivity(context, LoginActivity::class.java)
                            }
                        })
                } else {
                    ToastUtil.showShortToast(context.getString(R.string.network_error))
                }
            }
        }

        holder.commentBtn.setOnClickListener {
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastClickTime >= Constant.MIN_TIME_INTERVAL) {
//                if (TextUtils.isEmpty(Constant.CW_AUTHORIZATION)) {
//                    val bundle = Bundle()
//                    bundle.putString("from", Constant.PHONE_BIND)
//                    IntentUtil.startActivity(context, LoginActivity::class.java, bundle)
//                    return@setOnClickListener
//                }
//                val userInfo = SharedPreferenceUtil.getUserInfo()
//                if (null != userInfo) {
//                    if (TextUtils.isEmpty(userInfo.mobile)) {
//                        val bundle = Bundle()
//                        bundle.putString("from", Constant.PHONE_BIND)
//                        IntentUtil.startActivity(context, UpdateMobileActivity::class.java, bundle)
//                        return@setOnClickListener
//                    }
//                }
                showCommentDialog(item.id.toString())
            }
            lastClickTime = currentTime
        }
        holder.shareBtn.setOnClickListener {
            doPaikewShare(item)
        }
        holder.authorAvatar.setOnClickListener {
            showPaiKewInfo(item)
        }
        holder.promotionLayout.setOnClickListener {
            showPromotion(item.promotion_url)
        }
        holder.mPosition = position
    }

    private fun showPromotion(url: String) {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastClickTime >= Constant.MIN_TIME_INTERVAL) {
            val bundle = Bundle()
            bundle.putString(Constant.TAG_URL, url)
            IntentUtil.startActivity(context, PromotionDetailActivity::class.java, bundle)
        }
        lastClickTime = currentTime
    }

    private fun showPaiKewInfo(entity: ItemVideoListEntity.DataBean) {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastClickTime >= Constant.MIN_TIME_INTERVAL) {
            val bundle = Bundle()
            bundle.putString(Constant.TAG_ID, entity.uid.toString())
            IntentUtil.startActivity(context, UserPaikewActivity::class.java, bundle)
        }
        lastClickTime = currentTime
    }

    private fun doPaikewShare(entity: ItemVideoListEntity.DataBean) {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastClickTime >= Constant.MIN_TIME_INTERVAL) {
            if (!TextUtils.isEmpty(entity.share_url)) {
                val shareEntity = ShareEntity()
                shareEntity.type = "paikew"
                shareEntity.url = entity.share_url
                shareEntity.title =
                    if (TextUtils.isEmpty(entity.title)) Constant.TILE_SHARE else entity.title
                shareEntity.redirect_url = ""
                shareEntity.summary =
                    if (TextUtils.isEmpty(entity.nickname)) Constant.SUMMARY_SHARE else entity.nickname
                shareEntity.newsId = entity.id.toString()
                shareEntity.isPaikewShare = true
                shareEntity.picUrl = entity.cover
                shareEntity.paikewType = "video"
                shareEntity.paikewerUserName =
                    if (TextUtils.isEmpty(entity.nickname)) "" else entity.nickname
                val gson = Gson()
                val bundle = Bundle()
                bundle.putString(Constant.TAG_SHARE_CONTENT, gson.toJson(shareEntity))
                IntentUtil.startActivity(
                    context,
                    ShareActivity::class.java, bundle
                )
            } else {
                ToastUtil.showShortToast("暂时无法分享")
            }
        }
        lastClickTime = currentTime
    }

    private fun showCommentDialog(paikeId: String) {
//        val popupView = PaiKeCommentPop(context)
//        popupView.setPaiKeId(paikeId)
//        popupView.setLifeCycleOwner(owner)
//        XPopup.Builder(context).moveUpToKeyboard(false).isDestroyOnDismiss(true)
//            .asCustom(popupView).show()
        EventBus.getDefault().post(EventMessage("showCommentDialog",paikeId))
    }

    fun setData(videoBeans: List<ItemVideoListEntity.DataBean>) {
        if (!videoBeans.isNullOrEmpty()) {
            mVideoBeans = videoBeans
            notifyItemRangeChanged(0, mVideoBeans.size)
        }
    }

    fun addData(videoBeans: List<ItemVideoListEntity.DataBean>) {
        if (!videoBeans.isNullOrEmpty()) {
            val size = mVideoBeans.size
            mVideoBeans = videoBeans
            notifyItemRangeChanged(size, mVideoBeans.size)
        }
    }

    override fun getItemCount() = mVideoBeans.size

    class ViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var mPosition = 0
        var authorAvatar: RoundAngleImageView = itemView.findViewById(R.id.img_avatar_detail_paikew)
        var authorUserNameTv: TextView = itemView.findViewById(R.id.txt_username_detail_paikew)
        var worksDescribeTv: TextView = itemView.findViewById(R.id.txt_describe_detail_paikew)
        var priseImg: ImageView = itemView.findViewById(R.id.img_prise_detail_paikew)
        var priseCntTv: TextView = itemView.findViewById(R.id.txt_cnt_prise_detail_paikew)
        var commentCntTv: TextView = itemView.findViewById(R.id.txt_cnt_commend_detail_paikew)
        var priseBtn: LinearLayout = itemView.findViewById(R.id.ll_prise_detail_paikew)
        var commentBtn: LinearLayout = itemView.findViewById(R.id.ll_comment_detail_paikew)
        var shareBtn: LinearLayout = itemView.findViewById(R.id.ll_share_detail_paikew)
        val promotionLayout: LinearLayout = itemView.findViewById(R.id.ll_promotion_detail_paikew)
        val promotionTv: TextView = itemView.findViewById(R.id.txt_promotion_detail_paikew)

        init {
            itemView.tag = this
        }
    }

    companion object {
        /**
         * 当前是被赞状态； 需要进行取消点赞
         */
        private const val STATUS_PRISE_UN_SELECT = 2

        /**
         * 当前是未被赞状态； 需要进行点赞
         */
        private const val STATUS_PRISE_SELECT = 1
    }
}