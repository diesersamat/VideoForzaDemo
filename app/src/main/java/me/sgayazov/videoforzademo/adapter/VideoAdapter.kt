package me.sgayazov.videoforzademo.adapter

import android.content.Context
import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.SimpleExoPlayerView
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import me.sgayazov.videoforzademo.R


class VideoAdapter(context: Context, private val videosList: List<String>) : RecyclerView.Adapter<VideoViewHolder>() {

    private var playingViewHolder: VideoViewHolder? = null
    private var playingPos: Int? = null
    private var playingListener: Player.EventListener? = null
    private var playingSource: HlsMediaSource? = null
    private val bandwidthMeter = DefaultBandwidthMeter()
    private val dataSourceFactory = DefaultDataSourceFactory(context, Util.getUserAgent(context, null), bandwidthMeter)
    private var inflater: LayoutInflater = LayoutInflater.from(context)
    private var player = ExoPlayerFactory.newSimpleInstance(context, DefaultTrackSelector(AdaptiveTrackSelection.Factory(bandwidthMeter)))

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val view = inflater.inflate(R.layout.item_video, parent, false)
        return VideoViewHolder(view)
    }

    override fun getItemCount(): Int = videosList.size

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        //do nothing right now
    }

    fun switchVideoView(viewHolder: VideoViewHolder, position: Int) {
        if (playingPos == position) {
            return
        }
        val previousHolder = playingViewHolder
        if (previousHolder != null) {
            previousHolder.videoCover.visibility = View.VISIBLE
            previousHolder.loader.visibility = View.GONE
            SimpleExoPlayerView.switchTargetView(player, previousHolder.videoView, viewHolder.videoView)
            playingViewHolder = viewHolder
            playingPos = position
        } else {
            viewHolder.videoView.player = player
            playingViewHolder = viewHolder
            playingPos = position
        }
        viewHolder.loader.visibility = View.VISIBLE
        playingSource = HlsMediaSource.Factory(dataSourceFactory)
                .createMediaSource(Uri.parse(videosList[position]))
        player.prepare(playingSource)
        player.playWhenReady = true
        player.repeatMode = Player.REPEAT_MODE_ALL
        player.removeListener(playingListener)
        playingListener = object : Player.DefaultEventListener() {
            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                if (playbackState == Player.STATE_READY) {
                    viewHolder.videoCover.visibility = View.GONE
                } else {
                    viewHolder.videoCover.visibility = View.VISIBLE
                }
            }
        }
        player.addListener(playingListener)
    }
}

class VideoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    var videoView: SimpleExoPlayerView = itemView.findViewById(R.id.video_view)
    var videoCover: View = itemView.findViewById(R.id.video_cover)
    var loader: View = itemView.findViewById(R.id.loader)

    init {
        //to make controls always visible
        videoView.controllerShowTimeoutMs = 0
    }
}