package ro.upt.ac.chiuitter.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.item_chiuit.*
import kotlinx.android.synthetic.main.view_home.*

import ro.upt.ac.chiuitter.R
import ro.upt.ac.chiuitter.data.database.ChiuitDbStore
import ro.upt.ac.chiuitter.data.database.RoomDatabase
import ro.upt.ac.chiuitter.data.dummy.DummyChiuitStore
import ro.upt.ac.chiuitter.domain.Chiuit
import ro.upt.ac.chiuitter.view.ComposeActivity.Companion.EXTRA_TEXT
import ro.upt.ac.chiuitter.viewmodel.HomeViewModel
import ro.upt.ac.chiuitter.viewmodel.HomeViewModelFactory


class HomeActivity : AppCompatActivity() {

    private lateinit var viewModel: HomeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.view_home)

        fab_add.setOnClickListener { composeChiuit() }

        val factory = HomeViewModelFactory(ChiuitDbStore(RoomDatabase.getDb(this)))
        viewModel = ViewModelProviders.of(this, factory).get(HomeViewModel::class.java)

        initList()
    }

    private fun initList() {
        rv_chiuit_list.apply {
            layoutManager = LinearLayoutManager(this@HomeActivity)
        }

        viewModel.chiuitsLiveData.observe(this, Observer { chiuits ->
            // Instantiate an adapter with the received list and assign it to recycler view
            rv_chiuit_list.adapter = ChiuitRecyclerViewAdapter(chiuits, this::shareChiuit, this::deleteChiuit)
        })

        viewModel.retrieveChiuits()
    }

    private fun deleteChiuit(chiuit: Chiuit) {
        viewModel.removeChiuit(chiuit)
    }


    /*
    Defines text sharing/sending *implicit* intent, opens the application chooser menu
    and then starts a new activity which supports sharing/sending text.
     */
    private fun shareChiuit(chiuit: Chiuit) {
        val sendIntent = Intent().apply {

            action = Intent.ACTION_SEND;
            type = "text/plain";
            putExtra(Intent.EXTRA_TEXT, chiuit.description);

        }

        val intentChooser = Intent.createChooser(sendIntent, "")

        startActivity(intentChooser)
    }

    /*
    Defines an *explicit* intent which will be used to start ComposeActivity.
     */
    private fun composeChiuit() {

        val composeActivityIntent = Intent(this,ComposeActivity::class.java).apply {

        }


        // We start a new activity that we expect to return the acquired text as the result.

        startActivityForResult(composeActivityIntent, COMPOSE_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            COMPOSE_REQUEST_CODE -> if (resultCode == Activity.RESULT_OK) extractText(data)
            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun extractText(data: Intent?) {
        data?.let {


            val extractedText = data.getStringExtra(EXTRA_TEXT);


            if(extractedText.isNullOrEmpty()){
                Toast.makeText(this, "The text is null or empty, please try again !", Toast.LENGTH_SHORT).show();
            }else{
                viewModel.addChiuit(extractedText);
            }

        }
    }

    companion object {
        const val COMPOSE_REQUEST_CODE = 1213
    }

}