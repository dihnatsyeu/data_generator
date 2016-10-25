package data_for_entity.provider_resolver;

import data_for_entity.data_providers.EntityDataProvider;

public abstract class ProviderResolver {
   
    private ProviderResolver nextResolver;
    
    
    private ProviderResolver getNextResolver() {
        return nextResolver;
    }
    
    public void setNextResolver(ProviderResolver nextResolver) {
        this.nextResolver = nextResolver;
    }
    
    protected abstract EntityDataProvider getInternalProvider();
    
    
    public EntityDataProvider getProvider() {
        EntityDataProvider provider = getInternalProvider();
        if (provider != null) {
            return provider;
        }
        if (getNextResolver()!=null) {
            return getNextResolver().getProvider();
        }
        else return null;
    };
}
