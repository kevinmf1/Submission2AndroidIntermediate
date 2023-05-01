package com.example.submissionandroidintermediate.unit.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.submissionandroidintermediate.viewmodel.MainViewModel
import org.junit.Rule
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class MainViewModelTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var mainViewModel: MainViewModel


}