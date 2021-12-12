package com.alwaystinkering.sandbot.di

import android.content.Context
import com.alwaystinkering.sandbot.ui.main.MainFragment
import com.alwaystinkering.sandbot.ui.patterns.PatternsFragment
import com.alwaystinkering.sandbot.ui.playlistedit.SequenceEditFragment
import com.alwaystinkering.sandbot.ui.playlists.PlaylistsFragment
import com.alwaystinkering.sandbot.ui.preview.PatternPreviewFragment
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [SandbotModule::class])
interface AppComponent {
    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context): AppComponent
    }

    fun inject(fragment: MainFragment)
    fun inject(fragment: PatternsFragment)
    fun inject(fragment: PlaylistsFragment)
    fun inject(fragment: SequenceEditFragment)
    fun inject(fragment: PatternPreviewFragment)
}