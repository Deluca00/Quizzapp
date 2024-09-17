package np.com.bimalkafle.quizonline

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import np.com.bimalkafle.quizonline.Adapter.MeaningAdapter
import np.com.bimalkafle.quizonline.Class.WordResult
import np.com.bimalkafle.quizonline.databinding.FragmentDictionaryBinding
import java.lang.Exception

class DictionaryFragment : Fragment() {
    private lateinit var binding: FragmentDictionaryBinding
    private lateinit var adapter: MeaningAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDictionaryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.searchBtn.setOnClickListener {
            val word = binding.searchInput.text.toString()
            getMeaning(word)
        }

        adapter = MeaningAdapter(emptyList())
        binding.meaningRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.meaningRecyclerView.adapter = adapter
    }

    private fun getMeaning(word: String) {
        setInProgress(true)
        GlobalScope.launch {
            try {
                val response = RetrofitInstance.dictionaryApi.getMeaning(word)
                if (response.body() == null) {
                    throw (Exception())
                }
                requireActivity().runOnUiThread {
                    setInProgress(false)
                    response.body()?.first()?.let {
                        setUI(it)
                    }
                }
            } catch (e: Exception) {
                requireActivity().runOnUiThread {
                    setInProgress(false)
                    Toast.makeText(requireContext(), "Something went wrong", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setUI(response: WordResult) {
        binding.wordTextview.text = response.word
        binding.phoneticTextview.text = response.phonetic
        adapter.updateNewData(response.meanings)
    }

    private fun setInProgress(inProgress: Boolean) {
        if (inProgress) {
            binding.searchBtn.visibility = View.INVISIBLE
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.searchBtn.visibility = View.VISIBLE
            binding.progressBar.visibility = View.INVISIBLE
        }
    }
}
