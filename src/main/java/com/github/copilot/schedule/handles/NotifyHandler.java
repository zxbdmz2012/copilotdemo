package com.github.copilot.schedule.handles;

import com.github.copilot.schedule.enums.NotifyCmd;
import com.github.copilot.util.SpringContextUtil;


public interface NotifyHandler<T> {

    static NotifyHandler chooseHandler(NotifyCmd notifyCmd) {
        return SpringContextUtil.getByTypeAndName(NotifyHandler.class, notifyCmd.toString());
    }

    void update(T t);

}
