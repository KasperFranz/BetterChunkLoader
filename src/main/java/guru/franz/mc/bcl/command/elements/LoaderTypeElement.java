package guru.franz.mc.bcl.command.elements;

import com.google.common.collect.ImmutableList;
import org.spongepowered.api.text.Text;

import java.util.List;

public class LoaderTypeElement extends SimpleValueElement {

    public LoaderTypeElement(Text key) {
        super(key);
    }

    @Override
    protected List<String> getValues() {
        return ImmutableList.of("world", "personal");
    }
}