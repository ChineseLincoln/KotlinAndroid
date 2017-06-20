package unreal.org.ktapp.function.main.contract

import org.unreal.core.base.BasePresenter
import org.unreal.core.base.BaseView

/**
 * <b>类名称：</b> MainContract <br/>
 * <b>类描述：</b> <br/>
 * <b>创建人：</b> Lincoln <br/>
 * <b>修改人：</b> Lincoln <br/>
 * <b>修改时间：</b> 2017年05月25日 14:37<br/>
 * <b>修改备注：</b> <br/>
 *
 * @version 1.0.0 <br/>
 */
class MainContract{
    interface View : BaseView {
        fun result()
    }

    interface Presenter : BasePresenter{
        fun loginLoad(userName: String, passWord: String)

    }
}