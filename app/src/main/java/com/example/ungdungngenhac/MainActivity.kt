package com.example.ungdungngenhac

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.app.Dialog
import android.app.SearchManager
import android.content.*
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.database.sqlite.SQLiteDatabase
import android.media.AudioManager
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.PersistableBundle
import android.text.Html
import android.text.TextUtils
import android.util.Log
import android.view.*
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.TranslateAnimation
import android.widget.*

import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.view.menu.MenuBuilder
import androidx.appcompat.view.menu.MenuPopupHelper
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.core.graphics.toColor
import androidx.drawerlayout.widget.DrawerLayout
import com.example.ungdungngenhac.adapters.ViewPagerAdapter
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.app_main.*
import kotlinx.android.synthetic.main.nav_head_main.view.*
import androidx.core.view.GravityCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.ungdungngenhac.Base.BaseFragment
import com.example.ungdungngenhac.fragments.XepHangFragment
import com.example.ungdungngenhac.fragments.YeuCauFragment
import com.example.ungdungngenhac.fragments.YeuThichFragment
import com.example.ungdungngenhac.interfaces.Constants
import com.example.ungdungngenhac.interfaces.intent_data_play
import com.example.ungdungngenhac.models.BaiHat
import com.example.ungdungngenhac.notification.Notifications
import com.example.ungdungngenhac.notification.service.NotificationService

import com.facebook.AccessToken
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.OnCompleteListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.custom_dialog_timer.*
import java.io.IOException
import java.lang.IllegalArgumentException
import java.lang.IllegalStateException
import java.lang.NullPointerException
import java.text.DateFormat
import java.text.SimpleDateFormat


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,
    intent_data_play.online,
    intent_data_play.favorite,
    SeekBar.OnSeekBarChangeListener,XepHangFragment.sendData {


    override fun setDataSong(listData: ArrayList<BaiHat>) {

        this.dataBaiHatOnline = listData
        val inten:Intent=intent
        when(inten.action){
            Constants.ACTION.ACTION_MAIN -> {
                val baihat = inten.getSerializableExtra(Constants.KEY_INTENT_SONG_MAIN) as BaiHat

                for (i in 0 until listData.size){
                    if(listData.get(i).linkBaiHat.equals(baihat.linkBaiHat)){
                        this.position = i
                        break
                    }
                }
                Log.d("aaaaaa",baihat.linkBaiHat +";;;;;"+baihat.tenBaiHat)
                SendDataIntoPlayMusic(baihat)
                KhoiTaoMedia(baihat.linkBaiHat)
                //set event cho my_view_detail_play
                showDetail(true)
                supportActionBar?.hide()

                img_avatar_my_view_play.clearAnimation()
                img_avatar_my_view_play.startAnimation(animationrotate_my_view)
                ic_pause_play__my_view_detail_play.setImageResource(R.drawable.ic_pause_circle_outline)
                //set time totle bai hat
                setTimeTotle()
                UpdateTime()
                setisClickableFalse(true)

            }
        }
    }

    private var dataSongLike = MutableLiveData<ArrayList<BaiHat>>()

    fun getDataSongLike(): MutableLiveData<ArrayList<BaiHat>>{
        return dataSongLike
    }

    private var notify: Notifications? = null
    private var broadCastReceiver: BroadcastReceiver ?=null
    private var localBroadcastManager: LocalBroadcastManager? = null

    //Intent data nitfication
    private lateinit var inten: Intent
    private fun broadcastReceiver() {
        broadCastReceiver = object : BroadcastReceiver() {
            override fun onReceive(contxt: Context?, intent: Intent?) {
                when (intent?.action) {
                    Constants.ACTION.ACTION_BROADCAST -> {
                        when (intent.getStringExtra(Constants.KEY_INTENT_BROADCAST)) {
                            Constants.ACTION.ACTION_PREVIOUS -> {
                                previousBaiHat(ic_pause_play__my_view_detail_play)

                            }
                            Constants.ACTION.ACTION_PLAY -> {
                                eventActionPlayNoti()


                            }
                            Constants.ACTION.ACTION_NEXT -> {
                                nextBaiHat(ic_pause_play__my_view_detail_play)

                            }

                        }
                    }

                }
            }
        }
    }

    override fun onLongclick(data: ArrayList<BaiHat>, position: Int, namefm: String) {
//        notify!!.setBaiHat(data.get(position))
//        notify!!.builNotify()
//        notify!!.showNotify(123)
    }


    private var url: String? = null
    private var positionPage: Int = 0
    private var listenerBaihat: sendDataBaiHat? = null
    private var drawer: DrawerLayout? = null
    private var togger: ActionBarDrawerToggle? = null
    private var searchView: SearchView? = null
    private var adapter: ViewPagerAdapter? = null
    private var mGoogleSignInClient: GoogleSignInClient? = null

    private val fragmemtOnline: XepHangFragment = XepHangFragment()
    private val fragmemtOfline: YeuCauFragment = YeuCauFragment()
    private val fragmemtFavorite: YeuThichFragment = YeuThichFragment()
    private val fms: MutableList<BaseFragment> = mutableListOf(
        fragmemtOnline,
        fragmemtOfline,
        fragmemtFavorite
    )

    private var animationrotate_my_view: Animation? = null
    private var animationrotate_view_bottom: Animation? = null
    private var mediaPlayer: MediaPlayer? = null

    private var dataBaiHat: ArrayList<BaiHat>? = null
    private var dataBaiHatOnline: ArrayList<BaiHat>? = null
    private var dataBaiHatLike: ArrayList<BaiHat> = arrayListOf()

    private var position: Int? = null
    private val time: SimpleDateFormat = SimpleDateFormat("mm:ss")

    //send favorite
    private var listenersData: sendDataBaiHat? = null

    //Dialog CountDownTimer
    private var dialog: Dialog? = null
    private var countdowntimer: CountDownTimer? = null
    private var remainingTimer: Long = 0
    private val intervalTimer: Long = 1000
    private var didStartCountdownTimer: Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        dataSongLike.observe(this, Observer {
            this.dataBaiHatLike = it
        })

        fragmemtOnline.setlistenerDataSong(this)
        //TODO:click noti ACTION_MAIN


        //TODO:set title cho actionbar
        supportActionBar!!.setTitle(R.string.actionbar_title)
        supportActionBar!!.elevation = 0F

        //TODO: drawer navigation
        drawer = findViewById(R.id.drawer_layout)
        togger = ActionBarDrawerToggle(
            this,
            drawer,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawer?.addDrawerListener(togger!!)
        //2 dòng dưới để hiển thị hình bánh humburger
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        //TODO : set username avatar
        setNavigationHeader()
        //TODO: set sự kiện cho navigationview
        setNavigationViewListener()

        //TODO:set tablayout
        view_pager.offscreenPageLimit = 2
        adapter = ViewPagerAdapter(supportFragmentManager, fms)
        view_pager.adapter = adapter
        tab_layout.setupWithViewPager(view_pager)
        //set sự kiện onlcick fragment rồi truyền data về cho main
        fragmemtOnline.setDataLisener(this@MainActivity)
        fragmemtFavorite.setDataLisener(this@MainActivity)
        setupTabIcon()

        //TODO: Khởi tạo GoogleSignInClient
        var gso: GoogleSignInOptions =
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken("206660978551-ak4gonr8oqcbo93nsc5no6br26ondshf.apps.googleusercontent.com")
                .build()
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        //TODO: Bottom sheet and My_view detail play
        initViewPlay()

        initPage()

        //TODO:Khởi tạo dialog timer
        dialog = Dialog(this@MainActivity)
        dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog!!.setCancelable(true)
        dialog!!.setContentView(R.layout.custom_dialog_timer)
        dialog!!.getWindow()
            .setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        //TODO: push notification
        notify = Notifications.getInstance(this)
        val intentFilter = IntentFilter()
        intentFilter.addAction(Constants.ACTION.ACTION_BROADCAST);
        localBroadcastManager= LocalBroadcastManager.getInstance(this)
        //check action
        broadcastReceiver()
        //đăng kí action
        localBroadcastManager!!.registerReceiver(broadCastReceiver!!,intentFilter)


        inten= Intent(this@MainActivity, NotificationService::class.java)
    }

    private fun initPage() {
        view_pager.setOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {

            }

            override fun onPageSelected(position: Int) {
                positionPage = position
            }

        })

    }

    fun setDataBaiHat(listenersBaiHat: sendDataBaiHat) {
        this.listenersData = listenersBaiHat
    }

    private fun setisClickableFalse(status: Boolean) {
        liner_bottom_play.isFocusable = !status
        liner_bottom_play.isClickable = !status
        ic_down_my_view_detail_play.isClickable = status
        img_menu_detail_play.isClickable = status
        seekbar_my_view.isFocusable = status
        seekbar_my_view.isClickable = status
        ic_previous_my_view_detail_play.isClickable = status
        ic_pause_play__my_view_detail_play.isClickable = status
        ic_next_my_view_detail_play.isClickable = status
        my_view_detail_play.isClickable = status
    }

    private fun showDetail(status: Boolean) {
        if (status) {
            slideUp(my_view_detail_play)
            slideDown(liner_bottom_play)
        } else {
            slideUp(liner_bottom_play)
            slideDown(my_view_detail_play)
        }
    }

    //TODO:event ACTION_PLAY NOTI
    private fun eventActionPlayNoti(){
        if (mediaPlayer!!.isPlaying) {
            //neu dang phat pause-> doi sang hinh play
            img_avatar_my_view_play.clearAnimation()
            mediaPlayer!!.pause()
            ic_pause_play__my_view_detail_play.setImageResource(R.drawable.ic_play_circle_outline)

            img_bottom_play.clearAnimation()
            ic_pause_play__bottom_play.setImageResource(R.drawable.ic_play_circle_outline)
            notify!!.setPlaying(false)
        } else {
            //dang ngừng -> phát -> đổi hình pause
            img_avatar_my_view_play.startAnimation(animationrotate_my_view)
            mediaPlayer!!.start()
            ic_pause_play__my_view_detail_play.setImageResource(R.drawable.ic_pause_circle_outline)

            img_bottom_play.startAnimation(animationrotate_view_bottom)
            ic_pause_play__bottom_play.setImageResource(R.drawable.ic_pause_circle_outline)
            notify!!.setPlaying(true)
        }
    }
    private fun initViewPlay() {

        //Test notification
        notify = Notifications.getInstance(this)


        //event click view nhỏ nên view lớn play
        liner_bottom_play.setOnClickListener {
            supportActionBar?.hide()
            showDetail(true)

            if (mediaPlayer!!.isPlaying) {
                img_avatar_my_view_play.startAnimation(animationrotate_my_view)
                ic_pause_play__my_view_detail_play.setImageResource(R.drawable.ic_pause_circle_outline)
            } else {
                img_avatar_my_view_play.clearAnimation()
                ic_pause_play__my_view_detail_play.setImageResource(R.drawable.ic_play_circle_outline)
            }
            setisClickableFalse(true)
        }

        //disk quay
        animationrotate_my_view = AnimationUtils.loadAnimation(this, R.anim.disk_rotate)
        animationrotate_view_bottom = AnimationUtils.loadAnimation(this, R.anim.disk_rotate_bottom)

        //Ẩn my_view_detail_play và hiện view_bottom_play
        ic_down_my_view_detail_play.setOnClickListener {

            supportActionBar?.show()
            showDetail(false)

            my_view_detail_play.setClickable(false)
            my_view_detail_play.setFocusable(false)
            liner_bottom_play.isClickable = true
            liner_bottom_play.isFocusable = true
            setisClickableFalse(false)


            if (mediaPlayer!!.isPlaying) {
                img_bottom_play.startAnimation(animationrotate_view_bottom)
                ic_pause_play__bottom_play.setImageResource(R.drawable.ic_pause_circle_outline)
            } else {
                img_bottom_play.clearAnimation()
                ic_pause_play__bottom_play.setImageResource(R.drawable.ic_play_circle_outline)
            }

        }

        //even ic_pause_play__my_view_detail_play
        ic_pause_play__my_view_detail_play.setOnClickListener {
            if (mediaPlayer!!.isPlaying) {
                //neu dang phat pause-> doi sang hinh play
                img_avatar_my_view_play.clearAnimation()
                mediaPlayer!!.pause()
                ic_pause_play__my_view_detail_play.setImageResource(R.drawable.ic_play_circle_outline)
                notify!!.setPlaying(false)
            } else {
                //dang ngừng -> phát -> đổi hình pause
                img_avatar_my_view_play.startAnimation(animationrotate_my_view)
                mediaPlayer!!.start()
                ic_pause_play__my_view_detail_play.setImageResource(R.drawable.ic_pause_circle_outline)
                notify!!.setPlaying(true)
            }
        }
        //even ic_pause_play__view_bottom_play
        ic_pause_play__bottom_play.setOnClickListener {
            if (mediaPlayer!!.isPlaying) {
                //neu dang phat pause-> doi sang hinh play
                img_bottom_play.clearAnimation()
                mediaPlayer!!.pause()
                ic_pause_play__bottom_play.setImageResource(R.drawable.ic_play_circle_outline)
                notify!!.setPlaying(false)
            } else {
                //dang ngừng -> phát -> đổi hình pause
                img_bottom_play.startAnimation(animationrotate_view_bottom)
                mediaPlayer!!.start()
                ic_pause_play__bottom_play.setImageResource(R.drawable.ic_pause_circle_outline)
                notify!!.setPlaying(true)
            }

        }

        //event previous bai hat my_view_detail_play
        ic_previous_my_view_detail_play.setOnClickListener {
            previousBaiHat(ic_pause_play__my_view_detail_play)
        }
        //event next bai hat my_view_detail_play
        ic_next_my_view_detail_play.setOnClickListener {
            nextBaiHat(ic_pause_play__my_view_detail_play)
        }
        //event previous bai hat view Botom play
        ic_previous_bottom_play.setOnClickListener {
            previousBaiHat(ic_pause_play__bottom_play)
        }
        //event next bai hat view Botom play
        ic_next_bottom_play.setOnClickListener {
            nextBaiHat(ic_pause_play__bottom_play)
        }

        //event
        seekbar_my_view.setOnSeekBarChangeListener(this)

        //event menu my_view_play
        img_menu_detail_play.setOnClickListener {
            val popupMenu = PopupMenu(this, it)
            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.id_ic_detail_yeuthich -> {
                        listenersData!!.sendDatafavorite(dataBaiHat!!.get(this.position!!))
                        Toast.makeText(this, "Đã thêm vào mục yêu thích!", Toast.LENGTH_SHORT)
                            .show()
                        true
                    }
                    R.id.id_ic_detail_hengio -> {

                        showDialog()

                        true
                    }
                    else -> false
                }
            }

            popupMenu.inflate(R.menu.detail_menu)

            try {
                val fieldMPopup = PopupMenu::class.java.getDeclaredField("mPopup")
                fieldMPopup.isAccessible = true
                val mPopup = fieldMPopup.get(popupMenu)
                mPopup.javaClass
                    .getDeclaredMethod("setForceShowIcon", Boolean::class.java)
                    .invoke(mPopup, true)
            } catch (e: Exception) {
                Log.e("Main", "Error showing menu icons.", e)
            } finally {
                popupMenu.show()
            }
        }

    }

    //TODO: show custom dialog timer
    //BUG: Nếu nhập lần 1 ok,nhập lần 2 nếu số bé hơn thì sẽ mặc định là số lần 1
    private fun showDialog() {
        dialog!!.show()
        //check swick
        dialog!!.sw_timer.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                startCountDownTimer(20 * 60 * 1000, intervalTimer)
                didStartCountdownTimer = false
            } else {
                stopCountDownTimer()
                dialog!!.tv_time_timer.text = ""
            }
        }
        //even click dong ý timer
        dialog!!.btn_dongy_timer.setOnClickListener {

            if (didStartCountdownTimer  && dialog!!.ed_input_timer.text.toString().equals("") ) {
                dialog!!.dismiss()

            } else if (dialog!!.sw_timer.isChecked && dialog!!.ed_input_timer.text.toString().equals(
                    ""
                )
            ) {
                Toast.makeText(this, "Nhạc sẽ được tắt sau 20'", Toast.LENGTH_SHORT).show()
                dialog!!.dismiss()

            } else if (dialog!!.ed_input_timer.text.toString().equals("") && !dialog!!.sw_timer.isChecked) {
                dialog!!.dismiss()

            } else {
                dialog!!.sw_timer.isChecked = true
                remainingTimer = Integer.parseInt(dialog!!.ed_input_timer.text.toString()).toLong()
                remainingTimer = remainingTimer * 1000 * 60
                stopCountDownTimer()
                startCountDownTimer(remainingTimer, intervalTimer)
                Toast.makeText(
                    this,
                    "Nhạc sẽ được tắt sau " + dialog!!.ed_input_timer.text.toString() + " '",
                    Toast.LENGTH_SHORT
                ).show()
                dialog!!.dismiss()
                dialog!!.ed_input_timer.text.clear()


            }

        }

    }

    //Start Countdowntimer
    private fun startCountDownTimer(duration: Long, interval: Long) {
        countdowntimer = object : CountDownTimer(duration, interval) {
            override fun onTick(millisUntilFinished: Long) {
                remainingTimer = millisUntilFinished
                var seconds = millisUntilFinished / 1000
                var minutes = seconds / 60
                val hours = minutes / 60
                if (hours > 0)
                    minutes = minutes % 60

                val time = formatNumber(hours) + ":" + formatNumber(minutes) + "'"
                dialog!!.tv_time_timer.text = time
                if(minutes<1 && seconds>0){
                    dialog!!.tv_time_timer.text = "<00:01'"
                }
            }

            override fun onFinish() {
                if (mediaPlayer!!.isPlaying) {
                    img_avatar_my_view_play.clearAnimation()
                    mediaPlayer!!.pause()
                    ic_pause_play__my_view_detail_play.setImageResource(R.drawable.ic_play_circle_outline)
                }
                dialog!!.sw_timer.isChecked = false
                dialog!!.tv_time_timer.text = ""
                notify!!.setPlaying(false)
            }
        }
        countdowntimer!!.start()
        didStartCountdownTimer = true
    }

    //fomat về dạng 00:00
    private fun formatNumber(value: Long): String {
        if (value < 10)
            return "0$value"
        return "$value"
    }

    //Stop countdowntimer
    private fun stopCountDownTimer() {
        countdowntimer?.cancel()
        didStartCountdownTimer = false

    }

    override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
        tv_my_view_runtime.text = time.format(p1)
    }

    override fun onStartTrackingTouch(p0: SeekBar?) {
    }

    override fun onStopTrackingTouch(p0: SeekBar?) {
        mediaPlayer!!.seekTo(seekbar_my_view.progress)
    }

    //even previous bai hat
    private fun previousBaiHat(view: ImageView) {
        this.position = this.position!! - 1
        if (this.position!! < 0) {
            this.position = dataBaiHat!!.size - 1
        }
        if (mediaPlayer!!.isPlaying) {
            mediaPlayer!!.stop()
        }
        SendDataIntoPlayMusic(dataBaiHat!!.get(this.position!!))
        KhoiTaoMedia(dataBaiHat!!.get(this.position!!).linkBaiHat)
        view.setImageResource(R.drawable.ic_pause_circle_outline)
        setTimeTotle()
        UpdateTime()
        notify!!.setBaiHat(dataBaiHat!!.get(position!!))
        notify!!.setPlaying(true)

    }

    //even next bai hat
    private fun nextBaiHat(view: ImageView) {
        this.position = this.position!! + 1
        if (this.position!! > dataBaiHat!!.size - 1) {
            this.position = 0
        }
        if (mediaPlayer!!.isPlaying) {
            mediaPlayer!!.stop()
        }
        SendDataIntoPlayMusic(dataBaiHat!!.get(this.position!!))
        KhoiTaoMedia(dataBaiHat!!.get(this.position!!).linkBaiHat)
        view.setImageResource(R.drawable.ic_pause_circle_outline)
        setTimeTotle()
        UpdateTime()
        notify!!.setBaiHat(dataBaiHat!!.get(position!!))
        notify!!.setPlaying(true)

    }

    //set time totle
    private fun setTimeTotle() {
        tv_my_view_totle.text = time.format(mediaPlayer!!.duration)
        //gán seekbar time max
        seekbar_my_view.max = mediaPlayer!!.duration
    }

    //set update time bai hat
    private fun UpdateTime() {
        Handler().apply {
            val runnable = object : Runnable {
                override fun run() {
                    try {
                        tv_my_view_runtime.text = time.format(mediaPlayer!!.currentPosition)
                        Log.d("a", "" + mediaPlayer!!.currentPosition)
                        seekbar_my_view.progress = mediaPlayer!!.currentPosition
                    } catch (e: IllegalStateException) {

                    }

                    //kiểm tra time bài hát nếu hết -> next
                    mediaPlayer!!.setOnCompletionListener(MediaPlayer.OnCompletionListener {
                        nextBaiHat(ic_pause_play__my_view_detail_play)
                    })
                    postDelayed(this, 100)
                }
            }
            postDelayed(runnable, 100)
        }

    }

    //TODO: onclick play nhạc từ fragment online và fragment favorite (dùng chung)
    override fun onclick(position: Int, namefm: String) {
        if(namefm.equals(Constants.IS_ONLINE)){
            this.dataBaiHat = this.dataBaiHatOnline
        }else if(namefm.equals(Constants.IS_LIKE)){
            this.dataBaiHat = this.dataBaiHatLike
        }

        SendDataIntoPlayMusic(dataBaiHat!!.get(position))

//        if (this.position != position) {
//            if(mediaPlayer != null) {
//                mediaPlayer.apply {
//                    mediaPlayer!!.stop()
//                    mediaPlayer!!.release()
//                    mediaPlayer = null
//                }
//            }
//
//        }
//        if (this.position == position) {
//            if(mediaPlayer != null) {
//                mediaPlayer.apply {
//                    mediaPlayer!!.stop()
//                    mediaPlayer!!.release()
//                    mediaPlayer = null
//                }
//            }
//        }

        KhoiTaoMedia(dataBaiHat!!.get(position).linkBaiHat)
        this.position = position
        //set event cho my_view_detail_play
        slideUp(my_view_detail_play)
        slideDown(liner_bottom_play)
        supportActionBar?.hide()
        img_avatar_my_view_play.startAnimation(animationrotate_my_view)
        ic_pause_play__my_view_detail_play.setImageResource(R.drawable.ic_pause_circle_outline)
        //set time totle bai hat
        setTimeTotle()
        UpdateTime()
        setisClickableFalse(true)

        //TODO:set Action shownotification intent sang NotificationService class
        inten.putExtra("Song", this.dataBaiHat!!.get(position))
        inten.setAction(Constants.ACTION.ACTION_SHOW_NOTIFICATION)

        // check and  requireContext().startService(inten)
        if (isMyServiceRunning(NotificationService::class.java)) {
            this@MainActivity.stopService(inten)
        }
        ContextCompat.startForegroundService(this@MainActivity, inten!!)

    }
    fun isMyServiceRunning(calssObj: Class<NotificationService>): Boolean {
        val manager =
            this@MainActivity.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Integer.MAX_VALUE)) {
            if (calssObj.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @SuppressLint("LongLogTag")
    private fun KhoiTaoMedia(url: String) {
        mediaPlayer = MediaPlayer()
        mediaPlayer!!.setAudioStreamType(AudioManager.STREAM_MUSIC)
        try {
            mediaPlayer!!.setDataSource(url)
            mediaPlayer!!.prepare()
        } catch (e: IOException) {
            nextBaiHat(ic_pause_play__bottom_play)
        } catch (e1: IllegalArgumentException) {
            nextBaiHat(ic_pause_play__bottom_play)
        }
        // might take long! (for buffering, etc)
        mediaPlayer!!.start()

    }


    fun SendDataIntoPlayMusic(baihat: BaiHat) {
        url = baihat.linkBaiHat
        initViewData(baihat)



    }

    private fun initViewData(baihat: BaiHat) {
        val avatar: String = baihat.anhBaiHat
        val title: String = baihat.tenBaiHat
        val author: String = baihat.tenCaSi

        //Set view_bottom play
        Glide.with(this).load(avatar).apply(RequestOptions.circleCropTransform())
            .into(img_bottom_play)
        tv_title_bottom_play.text = title
        tv_casi_bottom_play.text = author

        //Set my_view_detail_play
        tv_my_view_detail_title_play.text = title
        tv_my_view_detail_tenCaSi_play.text = author
        Glide.with(this)
            .load(avatar)
            .apply(RequestOptions.circleCropTransform())
            .into(img_avatar_my_view_play)
    }


    fun slideUp(view: View) {
        view.setVisibility(View.VISIBLE)
        val animate = TranslateAnimation(
            0f, // fromXDelta
            0f, // toXDelta
            view.getHeight().toFloat(), // fromYDelta
            0f
        )                // toYDelta
        animate.duration = 500
        animate.fillAfter = true
        view.startAnimation(animate)
    }

    fun slideDown(view: View) {
        val animate = TranslateAnimation(
            0f, // fromXDelta
            0f, // toXDelta
            0f, // fromYDelta
            view.height.toFloat()
        ) // toYDelta
        animate.duration = 500
        animate.fillAfter = true
        view.startAnimation(animate)
    }


    private fun setNavigationHeader() {
        var pre: SharedPreferences = getSharedPreferences("my_account", MODE_PRIVATE)
        var username: String = pre.getString("username", "")
        var avartar: String = pre.getString("avatar", "")
        drawer?.nav_view?.getHeaderView(0)?.nav_header_textView!!.text = username
        Picasso.with(this)
            .load(avartar)
            .into(drawer?.nav_view?.getHeaderView(0)?.nav_header_imageview)
    }

    private fun setNavigationViewListener() {
        nav_view.setNavigationItemSelectedListener(this)
    }

    override fun onNavigationItemSelected(p0: MenuItem): Boolean {
        when (p0.itemId) {
            R.id.nav_item_one -> {
                Toast.makeText(this, "about app", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_item_two -> {
                Toast.makeText(this, "help", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_menu_dangxuat -> {

                if (AccessToken.getCurrentAccessToken() != null) {
                    LoginManager.getInstance().logOut()
                    startActivity(Intent(this@MainActivity, DangNhapActivity::class.java))
                    finish()
                    writesharepreferenced()
                    Toast.makeText(this, "Đăng xuất thành công!", Toast.LENGTH_SHORT).show()
                } else {
                    signOut()
                    revokeAccess()
                }

            }
        }
        //close navigation drawer
        drawer!!.closeDrawer(GravityCompat.START)
        return true
    }

    //    TODO: SignOUt google
    private fun signOut() {

        mGoogleSignInClient!!.signOut().addOnCompleteListener(this, OnCompleteListener {
            startActivity(Intent(this@MainActivity, DangNhapActivity::class.java))
            writesharepreferenced()
            finish()
            Toast.makeText(this, "Đăng xuất thành công!", Toast.LENGTH_SHORT).show()
        })
    }

    private fun revokeAccess() {
        mGoogleSignInClient!!.revokeAccess()

    }

    private fun writesharepreferenced() {
        var pre: SharedPreferences = getSharedPreferences("my_account", MODE_PRIVATE)
        var edit = pre?.edit()
        edit.clear()
        edit!!.putBoolean("Status_login", false)
        edit.commit();
    }


    private fun setupTabIcon() {
        tab_layout.getTabAt(0)!!.setIcon(R.drawable.ic_xephang)
        tab_layout.getTabAt(1)!!.setIcon(R.drawable.ic_yeucau)
        tab_layout.getTabAt(2)!!.setIcon(R.drawable.ic_yeuthich)

    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        togger?.syncState()

    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        togger!!.onConfigurationChanged(newConfig)
    }

    override fun onDestroy() {
        super.onDestroy()
        //hủy broadcast
        if(localBroadcastManager!=null){
            localBroadcastManager!!.unregisterReceiver(broadCastReceiver!!)
        }
        try {
            if (mediaPlayer!!.isPlaying()) {
                mediaPlayer!!.stop()
                mediaPlayer!!.release();
            }
        } catch (e: NullPointerException) {

        }
        if (isMyServiceRunning(NotificationService::class.java)) {
            this@MainActivity.stopService(inten)
        }


    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        val searchmanager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        searchView = menu?.findItem(R.id.action_search)!!.actionView as SearchView
        searchView!!.setSearchableInfo(searchmanager.getSearchableInfo(componentName))
        searchView!!.setQueryHint("Tìm kiếm ...")
        searchView!!.setQueryHint(
            Html.fromHtml(
                "<font color = #ffffff>" + getResources().getString(
                    R.string.search_hint
                ) + "</font>"
            )
        );
        if (!searchView!!.isFocused()) {
            searchView!!.clearFocus();
        }
        searchView!!.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                // filter recycler view when query submitted
//                mAdapter!!.filter.filter(query)
                return false
            }

            override fun onQueryTextChange(query: String): Boolean {
                if (TextUtils.isEmpty(query)) {
                    fragmemtOnline.xephangadapter.filter("")
                    fragmemtFavorite.favoriteAdapter.filter("")


                } else {
                    when (positionPage) {
                        0 -> fragmemtOnline.xephangadapter.filter(query)
                        1 -> Toast.makeText(
                            this@MainActivity,
                            "chưa có bài hát nào!",
                            Toast.LENGTH_SHORT
                        ).show()
                        2 -> fragmemtFavorite.favoriteAdapter.filter(query)
                    }
                }
                return true
            }
        })
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val id = item!!.itemId
        when (id) {
            R.id.action_search -> {
                return true
            }
        }
        if (togger!!.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

    interface sendDataBaiHat {
        fun sendDatafavorite(data: BaiHat)
    }
}
