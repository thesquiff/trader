package com.futurewebdynamics.trader.postanalysers;

import com.futurewebdynamics.trader.positions.Position;

/**
 * Created by Charlie on 12/05/2017.
 */
public interface IPostAnalyser {

    void AnalysePosition(Position position);

}
