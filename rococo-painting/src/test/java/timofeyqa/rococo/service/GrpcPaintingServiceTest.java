package timofeyqa.rococo.service;

import com.google.protobuf.ByteString;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.*;
import timofeyqa.grpc.rococo.*;
import timofeyqa.grpc.rococo.Pageable;
import timofeyqa.rococo.data.PaintingEntity;
import timofeyqa.rococo.data.repository.PaintingRepository;
import timofeyqa.rococo.mappers.PaintingPatcher;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SuppressWarnings("unchecked")
class GrpcPaintingServiceTest {

  @Mock
  private PaintingRepository paintingRepository;

  @Mock
  private PaintingPatcher paintingPatcher;

  @InjectMocks
  private GrpcPaintingService grpcPaintingService;

  @Captor
  private ArgumentCaptor<Painting> paintingCaptor;

  @Captor
  private ArgumentCaptor<PagePainting> pagePaintingCaptor;

  @BeforeEach
  void setup() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void getPainting_returnsPainting() {
    UUID id = UUID.randomUUID();
    PaintingEntity entity = new PaintingEntity()
        .setId(id)
        .setTitle("Test")
        .setArtistId(UUID.randomUUID())
        .setContent("image".getBytes());

    when(paintingRepository.findById(id)).thenReturn(Optional.of(entity));

    StreamObserver<Painting> observer = mock(StreamObserver.class);

    grpcPaintingService.getPainting(Uuid.newBuilder().setUuid(id.toString()).build(), observer);

    verify(observer).onNext(paintingCaptor.capture());
    verify(observer).onCompleted();

    Painting painting = paintingCaptor.getValue();
    assertEquals(id.toString(), painting.getId());
    assertEquals("Test", painting.getTitle());
    assertFalse(painting.getContent().isEmpty());
  }

  @Test
  void getPaintingsPage_returnsPage() {
    PaintingEntity entity = new PaintingEntity()
        .setId(UUID.randomUUID())
        .setTitle("PageTest")
        .setArtistId(UUID.randomUUID())
        .setContent("img".getBytes());

    Page<PaintingEntity> page = new PageImpl<>(List.of(entity), PageRequest.of(0, 10), 1);

    when(paintingRepository.findAll(PageRequest.of(0, 10))).thenReturn(page);

    StreamObserver<PagePainting> observer = mock(StreamObserver.class);

    grpcPaintingService.getPaintingsPage(
        Pageable.newBuilder().setPage(0).setSize(10).build(), observer
    );

    verify(observer).onNext(pagePaintingCaptor.capture());
    verify(observer).onCompleted();

    PagePainting result = pagePaintingCaptor.getValue();
    assertEquals(1, result.getTotalElements());
    assertEquals("PageTest", result.getPaintings(0).getTitle());
  }

  @Test
  void getPaintingsByArtist_returnsFilteredPage() {
    UUID artistId = UUID.randomUUID();

    PaintingEntity entity = new PaintingEntity()
        .setId(UUID.randomUUID())
        .setTitle("ArtistPaint")
        .setArtistId(artistId)
        .setContent("x".getBytes());

    Page<PaintingEntity> page = new PageImpl<>(List.of(entity));

    when(paintingRepository.findByArtistId(eq(artistId), any()))
        .thenReturn(page);

    StreamObserver<PagePainting> observer = mock(StreamObserver.class);

    GetPaintingsByArtistRequest request = GetPaintingsByArtistRequest.newBuilder()
        .setUuid(Uuid.newBuilder().setUuid(artistId.toString()))
        .setPageable(Pageable.newBuilder().setPage(0).setSize(5))
        .build();

    grpcPaintingService.getPaintingsByArtist(request, observer);

    verify(observer).onNext(pagePaintingCaptor.capture());
    verify(observer).onCompleted();

    PagePainting result = pagePaintingCaptor.getValue();
    assertEquals(1, result.getPaintingsCount());
    assertEquals("ArtistPaint", result.getPaintings(0).getTitle());
  }

  @Test
  void updatePainting_appliesChanges() {
    UUID paintingId = UUID.randomUUID();
    PaintingEntity existing = new PaintingEntity()
        .setId(paintingId)
        .setTitle("Old")
        .setArtistId(UUID.randomUUID());

    Painting updatedProto = Painting.newBuilder()
        .setId(paintingId.toString())
        .setTitle("New Title")
        .setArtistId(existing.getArtistId().toString())
        .setContent(ByteString.copyFrom("newdata".getBytes()))
        .build();

    when(paintingRepository.findById(paintingId)).thenReturn(Optional.of(existing));
    when(paintingRepository.save(any())).thenAnswer(i -> i.getArgument(0));

    // Мокируем patcher для GRPC -> сущность
    doAnswer(invocation -> {
      Painting proto = invocation.getArgument(0, Painting.class);
      PaintingEntity entity = invocation.getArgument(1, PaintingEntity.class);
      entity.setTitle(proto.getTitle());
      entity.setArtistId(UUID.fromString(proto.getArtistId()));
      entity.setContent(proto.getContent().toByteArray());
      return null;
    }).when(paintingPatcher).patch(isA(Painting.class), isA(PaintingEntity.class), any());

    StreamObserver<Painting> observer = mock(StreamObserver.class);
    ArgumentCaptor<Painting> paintingCaptor = ArgumentCaptor.forClass(Painting.class);

    // Вызов сервиса
    grpcPaintingService.updatePainting(updatedProto, observer);

    // Проверка, что observer получил обновлённую сущность
    verify(observer).onNext(paintingCaptor.capture());
    verify(observer).onCompleted();

    Painting result = paintingCaptor.getValue();
    assertEquals("New Title", result.getTitle());
    assertEquals(updatedProto.getArtistId(), result.getArtistId());
    assertArrayEquals(updatedProto.getContent().toByteArray(), result.getContent().toByteArray());
  }


}
