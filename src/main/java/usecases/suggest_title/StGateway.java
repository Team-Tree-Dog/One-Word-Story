package usecases.suggest_title;

import usecases.like_story.LsGatewayInputData;

public interface StGateway {
    StGatewayInputData suggestTitle(LsGatewayInputData d);
}
