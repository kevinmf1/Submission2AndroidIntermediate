package com.example.submissionandroidintermediate.widget

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import androidx.core.os.bundleOf
import com.bumptech.glide.Glide
import com.example.submissionandroidintermediate.R
import kotlinx.coroutines.runBlocking

internal class StackRemoteViewsFactory(
    private val mContext: Context
) : RemoteViewsService.RemoteViewsFactory {

    private var mWidgetItems = listOf<StoryItemEntity>()
    private lateinit var dao: StoryDao

    override fun onCreate() {
        dao = StoryDatabase.getInstance(mContext.applicationContext).getStoryDao()
        fetchDataDB()
    }

    override fun onDataSetChanged() {
        fetchDataDB()
    }

    override fun onDestroy() {}

    override fun getCount(): Int = mWidgetItems.size

    override fun getViewAt(position: Int): RemoteViews {
        val rv = RemoteViews(mContext.packageName, R.layout.widget_item)
        try {
            val bitmap: Bitmap = Glide.with(mContext.applicationContext)
                .asBitmap()
                .load(mWidgetItems[position].photoUrl)
                .submit()
                .get()
            rv.setImageViewBitmap(R.id.iv_item, bitmap)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val extras = bundleOf(
            StoryWidget.EXTRA_ITEM to position
        )
        val fillIntent = Intent()
        fillIntent.putExtras(extras)

        rv.setOnClickFillInIntent(R.id.iv_item, fillIntent)
        return rv
    }

    override fun getLoadingView(): RemoteViews? = null

    override fun getViewTypeCount(): Int = 1

    override fun getItemId(p0: Int): Long = 0

    override fun hasStableIds(): Boolean = false

    private fun fetchDataDB() {
        runBlocking {
            mWidgetItems = dao.getAllAsList()
        }
    }
}