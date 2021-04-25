package app.xash.upisample.ui.addvpa

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.add
import androidx.fragment.app.commit
import app.xash.upisample.R
import app.xash.upisample.databinding.ActivityAddVpaBinding

class AddVpaActivity : AppCompatActivity() {

    val viewModel: AddVpaViewModel by viewModels()
    lateinit var binding: ActivityAddVpaBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddVpaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                setReorderingAllowed(true)
                add<SelectBankFragment>(R.id.fragment_container_view)
            }
        }
    }

}