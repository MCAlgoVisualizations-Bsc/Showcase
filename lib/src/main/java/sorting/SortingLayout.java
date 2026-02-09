package sorting;

import graphs.Graph;
import net.minestom.server.coordinate.Pos;

public interface SortingLayout {
    Pos[] compute(IntListModel model, Pos origin);
}
