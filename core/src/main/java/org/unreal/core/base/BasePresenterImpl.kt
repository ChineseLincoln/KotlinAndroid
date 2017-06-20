package org.unreal.core.base

import org.unreal.core.di.component.CoreComponent


/**
 * <b>类名称：</b> BasePresenterImpl <br/>
 * <b>类描述：</b> <br/>
 * <b>创建人：</b> Lincoln <br/>
 * <b>修改人：</b> Lincoln <br/>
 * <b>修改时间：</b> 2017年05月25日 16:40<br/>
 * <b>修改备注：</b> <br/>
 *
 * @version 1.0.0 <br/>
 */
abstract class BasePresenterImpl<out V : BaseView>(val view: V) : BasePresenter  {

    init {
        injectComponent(BaseApplication.coreComponent)
    }

    abstract fun injectComponent(coreComponent: CoreComponent)
}