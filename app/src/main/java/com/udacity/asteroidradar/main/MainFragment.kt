package com.udacity.asteroidradar.main

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.squareup.picasso.Picasso
import com.udacity.asteroidradar.PictureOfDay
import com.udacity.asteroidradar.R
import com.udacity.asteroidradar.api.AsteroidNetwork
import com.udacity.asteroidradar.api.NetworkPod
import com.udacity.asteroidradar.api.asDomainModel
import com.udacity.asteroidradar.databinding.FragmentMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import timber.log.Timber

class MainFragment : Fragment() {

    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(this).get(MainViewModel::class.java)
    }
    lateinit var netRes : String
    lateinit var pod: PictureOfDay
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        val binding = FragmentMainBinding.inflate(inflater)
        binding.lifecycleOwner = this

        binding.viewModel = viewModel

        setHasOptionsMenu(true)


        viewLifecycleOwner.lifecycleScope.launch{
            netRes = getResponse().string()
        }.invokeOnCompletion {
            //binding.textView.text = netRes
        }
        viewLifecycleOwner.lifecycleScope.launch{
            Timber.i("invoking getPod()")
            pod = getPod()
            Timber.i("pod is $pod")
        }.invokeOnCompletion {
            Timber.i("invokeOnCompletion for pod entered")
            Timber.i("pod is $pod")
            if (pod.mediaType=="image"){
                Timber.i("media_type is image")
                Timber.i(pod.url)
                binding.textView.text = pod.title
                Picasso.get().load(pod.url).into(binding.activityMainImageOfTheDay)
            }
        }

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_overflow_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return true
    }

    suspend fun getResponse():  ResponseBody   {
        return AsteroidNetwork.asteroidService.getAsteroids("2020-12-25", "2020-12-26", getString(R.string.neoWs_key)).await()
    }
    suspend fun getPod(): PictureOfDay{
        return AsteroidNetwork.asteroidService.getPod(apiKey = getString(R.string.neoWs_key)).await().asDomainModel()
    }
}
