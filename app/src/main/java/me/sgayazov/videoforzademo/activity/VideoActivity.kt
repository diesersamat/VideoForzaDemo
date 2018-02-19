package me.sgayazov.videoforzademo.activity

import android.graphics.Rect
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import me.sgayazov.videoforzademo.R
import me.sgayazov.videoforzademo.adapter.VideoAdapter
import me.sgayazov.videoforzademo.adapter.VideoViewHolder


class VideoActivity : AppCompatActivity() {

    private lateinit var videoRecycler: RecyclerView
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var adapter: VideoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video)
        videoRecycler = findViewById(R.id.video_recycler)
        layoutManager = videoRecycler.layoutManager as LinearLayoutManager
        videoRecycler.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        setDemoVideosList()
    }


    private fun setDemoVideosList() {
        //todo test values
        val videosList = listOf(
                "https://d2nsbgzitfs2gt.cloudfront.net/vods3/_definst_/mp4:amazons3/pitch-video-prod/22b1747fa3074b5482e60bf9c0fb25eb500bd0a2/playlist.m3u8",
                "https://d2nsbgzitfs2gt.cloudfront.net/vods3/_definst_/mp4:amazons3/pitch-video-prod/a2f95065ac92313c3bf5d053d7779e5185f0aea4/playlist.m3u8",
                "https://d2nsbgzitfs2gt.cloudfront.net/vods3/_definst_/mp4:amazons3/pitch-video-prod/5c1d2de9407ea0935c9ca49f5c8f45bbf9646dac/playlist.m3u8"
        )
        adapter = VideoAdapter(this, videosList)
        videoRecycler.adapter = adapter
        videoRecycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    playVideo()
                }
            }
        })
        videoRecycler.viewTreeObserver.addOnGlobalLayoutListener({ playVideo() })
    }

    private fun playVideo() {
        val firstVisiblePos = layoutManager.findFirstVisibleItemPosition()
        val lastVisiblePos = layoutManager.findLastVisibleItemPosition()
        val posToPlay = when {
        //there is only one item visible
            firstVisiblePos == lastVisiblePos -> firstVisiblePos
        //we have two visible items, let's decide which one takes more space on the screen
            firstVisiblePos + 1 == lastVisiblePos -> {
                val square1 = Rect()
                val square2 = Rect()
                (videoRecycler.findViewHolderForLayoutPosition(firstVisiblePos) as VideoViewHolder).itemView.getGlobalVisibleRect(square1)
                (videoRecycler.findViewHolderForLayoutPosition(lastVisiblePos) as VideoViewHolder).itemView.getGlobalVisibleRect(square2)
                if (square1.width() * square1.height() > square2.width() * square2.height()) {
                    firstVisiblePos
                } else {
                    lastVisiblePos
                }
            }
        //we have more than two visible items, let's find the middle item and play video in it
            else -> (firstVisiblePos + lastVisiblePos) / 2
        }

        if (posToPlay != RecyclerView.NO_POSITION) {
            adapter.switchVideoView(videoRecycler.findViewHolderForLayoutPosition(posToPlay) as VideoViewHolder, posToPlay)
        }
    }
}
