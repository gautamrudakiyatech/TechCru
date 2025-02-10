package com.tc.tech.cru.techcrunch.techcru;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.tc.tech.cru.techcrunch.techcru.databinding.FragmentFrag3Binding;

public class Frag3 extends Fragment {

    FragmentFrag3Binding binding;
    DataFetch viewModel = new DataFetch();

    public static Frag3 newInstance() {
        return new Frag3();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentFrag3Binding.inflate(inflater, container, false);
        View root = binding.getRoot();

//        viewModel = new ViewModelProvider(this).get(DataFetch.class);

//        binding.shimmer.setVisibility(View.VISIBLE);

        Thread.setDefaultUncaughtExceptionHandler(new CustomExceptionHandler(requireActivity()));

        binding.refresh.setOnRefreshListener(viewModel::loadContent);
        viewModel.urlsWeb("https://techcrunch.com/category/venture/");
        viewModel.recy(binding.recyclerView);
        viewModel.shim(binding.shimmer);
        viewModel.textPre(binding.teprev);
        viewModel.textnext(binding.tenext);
        viewModel.acti(requireActivity());
        viewModel.sear(binding.search);
        viewModel.refresh(binding.refresh);
        viewModel.layoutSetRecyc();
        viewModel.swipe();
        viewModel.goneText();
        viewModel.nextCard(binding.next);
        viewModel.peviousCard(binding.previous);
        viewModel.dataFet();
        viewModel.shuffleTextBoxColor();
        viewModel.callBG();

//        viewModel.liveData.observe(getViewLifecycleOwner(), parseItems -> {
//            // Update UI with new data
//        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
