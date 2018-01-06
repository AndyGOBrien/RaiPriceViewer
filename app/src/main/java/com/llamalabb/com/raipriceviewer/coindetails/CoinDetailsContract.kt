package com.llamalabb.com.raipriceviewer.coindetails

import com.llamalabb.com.raipriceviewer.BasePresenter
import com.llamalabb.com.raipriceviewer.BaseView

/**
 * Created by andy on 1/5/18.
 */
interface CoinDetailsContract {

    interface CoinDetailsView : BaseView<CoinDetailsPresenter>{

    }

    interface CoinDetailsPresenter : BasePresenter{

    }

}