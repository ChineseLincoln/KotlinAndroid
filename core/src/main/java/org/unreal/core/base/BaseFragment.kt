package org.unreal.core.base

import android.app.Activity
import android.os.Bundle
import android.support.annotation.Nullable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.trello.rxlifecycle2.components.support.RxFragment
import io.reactivex.disposables.CompositeDisposable
import org.jetbrains.anko.AnkoLogger
import org.unreal.core.di.component.CoreComponent
import org.unreal.core.manager.ActivityTaskManager
import org.unreal.widget.window.WaitScreen
import java.util.*
import javax.inject.Inject



/**
 * <b>类名称：</b> BaseFragment <br/>
 * <b>类描述：</b> <br/>
 * <b>创建人：</b> Lincoln <br/>
 * <b>修改人：</b> Lincoln <br/>
 * <b>修改时间：</b> 2017年05月25日 17:00<br/>
 * <b>修改备注：</b> <br/>
 *
 * @version 1.0.0 <br/>
 */
abstract class BaseFragment<P : BasePresenter> : RxFragment(), BaseView, AnkoLogger {

    @Inject
    lateinit var presenter: P

    private var waitScreens: Stack<WaitScreen> = Stack()

    lateinit var compositeDisposable: CompositeDisposable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        injectDagger(BaseApplication.coreComponent)
        compositeDisposable = CompositeDisposable()
    }

    override fun onCreateView(inflater: LayoutInflater?, @Nullable container: ViewGroup?, @Nullable savedInstanceState: Bundle?): View? {
        val view = inflater?.inflate(bindLayout(), container, false)
        return view
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        afterViews()
    }

    protected abstract fun afterViews()

    protected abstract fun bindLayout(): Int

    protected abstract fun injectDagger(appComponent: CoreComponent)

    override fun showWait() {
        val waitScreen = WaitScreen(activity)
        waitScreens.push(waitScreen)
        waitScreen.show()
    }

    override fun showWait(message: String) {
        val waitScreen = WaitScreen(activity)
        waitScreens.push(waitScreen)
        waitScreen.show(message)
    }

    override fun hideWait(onAnimationEnd: () -> Unit) {
        val waitScreen = waitScreens.pop()
        waitScreen.close(onAnimationEnd)
    }

    override fun closeWait() {
        val waitScreen = waitScreens.pop()
        waitScreen.dismiss()
    }

    override fun finish() {
        activity.finish()
    }

    override fun finishAll() {
        ActivityTaskManager.instance.finishAllActivities()
    }

    @SafeVarargs
    override fun finish(vararg activityClasses: Class<out Activity>) {
        ActivityTaskManager.instance.finishActivities(*activityClasses)
    }

    override fun onDestroy() {
        super.onDestroy()
        while (!waitScreens.empty()) {
            val waitScreen = waitScreens.pop()
            waitScreen.dismiss()
        }
    }
}