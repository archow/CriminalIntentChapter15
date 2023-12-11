package com.example.criminalintentchapter15

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.criminalintentchapter15.databinding.FragmentCrimeListBinding
import com.example.criminalintentchapter15.viewmodels.CrimeListViewModel
import kotlinx.coroutines.launch
import java.util.Date
import java.util.UUID

class CrimeListFragment : Fragment() {
    private var _binding: FragmentCrimeListBinding? = null
    private val binding: FragmentCrimeListBinding
        get() = checkNotNull(_binding) {
            "Binding should not be null. Is the view visible?"
        }
    private val crimeListViewModel : CrimeListViewModel by viewModels()
    private lateinit var crimeListAdapter : CrimeListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //instead of this, use menuHost
//        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCrimeListBinding.inflate(layoutInflater)
        crimeListAdapter = CrimeListAdapter {
            findNavController().navigate(
                CrimeListFragmentDirections.showCrime(it)
            )
        }
        binding.crimeRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = crimeListAdapter
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                crimeListViewModel.crimes.collect {
                    crimeListAdapter.submitList(it)
                }
            }
        }

        //since createOptionsMenu has been deprecated as of September 2022,
        //use MenuHost instead, like so:
        (requireActivity() as MenuHost).addMenuProvider(object: MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.fragment_crime_list, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.new_crime -> {
                        showNewCrime()
                        true
                    }
                    else -> true
                }
            }
        },
            //here we also supply the lifecycle owner and the Lifecycle state where we
            //want the menu to be visible, and we supply these to the
            //addMenuProvider(...)
            viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showNewCrime() {
        viewLifecycleOwner.lifecycleScope.launch {
            val newCrime = Crime(id= UUID.randomUUID(), title="", date= Date(), isSolved = false)
            crimeListViewModel.addCrime(newCrime)
            findNavController().navigate(
                CrimeListFragmentDirections.showCrime(newCrime.id)
            )
        }
    }
}