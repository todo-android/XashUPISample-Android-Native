package app.xash.upisample.addvpa

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import app.xash.upisample.BankAccountListAdapter
import app.xash.upisample.DeviceBindingViewModel
import app.xash.upisample.databinding.SelectBankFragmentBinding

class SelectBankFragment : Fragment() {
    private lateinit var binding: SelectBankFragmentBinding
    private val viewModel: DeviceBindingViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = SelectBankFragmentBinding.inflate(inflater, container, false)
        viewModel.fetchBankProviders()
        return binding.root
    }

    var bankAdapter: BankAccountListAdapter = BankAccountListAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.selectBankRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            addItemDecoration(SpacesItemDecoration(16, RecyclerView.HORIZONTAL))
            adapter = bankAdapter
        }
        viewModel.providers.observe(viewLifecycleOwner, bankAdapter::submitList)
    }
}