package com.xiaoma.redismq.remoting.client;

import com.xiaoma.redismq.common.data.TopicRouteData;

public interface RouteService {
    void updateTopicRouteInfo(final String topic, final TopicRouteData routeData);
}
