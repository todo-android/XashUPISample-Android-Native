package app.xash.upisample.vpa

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import app.xash.upisample.databinding.SelectBankForVpaFragmentBinding

class SelectBankForVpaFragment : Fragment() {

    lateinit var binding: SelectBankForVpaFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = SelectBankForVpaFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

}