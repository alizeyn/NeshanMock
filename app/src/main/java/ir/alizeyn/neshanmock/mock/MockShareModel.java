package ir.alizeyn.neshanmock.mock;

import java.io.Serializable;
import java.util.List;

import ir.alizeyn.neshanmock.database.MockEntity;
import ir.alizeyn.neshanmock.database.PosEntity;

/**
 * @author alizeyn
 * Created at 4/7/19
 */
public class MockShareModel implements Serializable {

    private MockEntity mockEntity;
    private List<PosEntity> mockPoses;

    public MockShareModel(MockEntity mockEntity, List<PosEntity> mockPoses) {
        this.mockEntity = mockEntity;
        this.mockPoses = mockPoses;
    }

    public MockEntity getMockEntity() {
        return mockEntity;
    }

    public void setMockEntity(MockEntity mockEntity) {
        this.mockEntity = mockEntity;
    }

    public List<PosEntity> getMockPoses() {
        return mockPoses;
    }

    public void setMockPoses(List<PosEntity> mockPoses) {
        this.mockPoses = mockPoses;
    }
}
