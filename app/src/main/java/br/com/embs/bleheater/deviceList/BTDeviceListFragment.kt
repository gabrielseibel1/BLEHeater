package br.com.embs.bleheater.deviceList

import android.bluetooth.BluetoothDevice
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import br.com.embs.bleheater.R

/**
 * A fragment representing a list of Items.
 * Activities containing this fragment MUST implement the
 * [BTDeviceListFragment.OnListFragmentInteractionListener] interface.
 */
class BTDeviceListFragment : Fragment() {

    private var listener: OnListFragmentInteractionListener? = null

    public val deviceListAdapter = BTDeviceListAdapter(mutableListOf(), listener)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_btdevice_list, container, false)

        // Set the adapter
        if (view is RecyclerView) {
            view.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = deviceListAdapter
            }
        }
        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnListFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnListFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson
     * [Communicating with Other Fragments](http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnListFragmentInteractionListener {
        fun onDeviceSelected(item: BluetoothDevice)
    }

    companion object {
        @JvmStatic
        fun newInstance(listListener: OnListFragmentInteractionListener)
                = BTDeviceListFragment().apply { listener = listListener}
    }
}
