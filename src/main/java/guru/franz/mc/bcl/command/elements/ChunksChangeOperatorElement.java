package guru.franz.mc.bcl.command.elements;

import com.google.common.collect.ImmutableList;
import org.spongepowered.api.text.Text;

import java.util.List;

public class ChunksChangeOperatorElement extends SimpleValueElement {

    public ChunksChangeOperatorElement(Text key) {
        super(key);
    }

    @Override
    protected List<String> getValues() {
        return ImmutableList.of("set", "add", "remove");
    }
}
