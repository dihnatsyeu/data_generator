package data_for_entity.data_providers;

import java.util.Collection;
import java.util.StringJoiner;

public class SequenceDataProvider extends DependencyDataProvider {
    
    @Override
    public Object generate(int length) {
        StringJoiner joiner = new StringJoiner(" ");
        DependencyData dependencyData = getDependencyData();
        Collection<String> values = dependencyData.getValues();
        values.forEach(joiner::add);
        return joiner.toString();
    }
}
