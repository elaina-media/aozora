package net.mikoto.aozora;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.log4j.Log4j2;
import net.mikoto.aozora.mapper.ArtworkMapper;
import net.mikoto.aozora.model.Artwork;
import net.mikoto.aozora.service.ArtworkService;
import org.apache.logging.log4j.Level;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.MyBatisSystemException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.List;

@SpringBootTest
@Log4j2
class AozoraApplicationTests {
	private final ArtworkService artworkService;
	private final ArtworkMapper artworkMapper;

	@Autowired
	AozoraApplicationTests(ArtworkService artworkService, ArtworkMapper artworkMapper) {
		this.artworkService = artworkService;
		this.artworkMapper = artworkMapper;
	}

	@Test
	void contextLoads() {
		log.log(Level.INFO, artworkService.getRemoteArtwork(20));
	}

	@Test
	void refreshData() throws IOException {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		File file =new File("err.txt");

		FileOutputStream fileOutputStream = new FileOutputStream(file);
		for (int i = 100; i < 1000; i++) {
			QueryWrapper<Artwork> queryWrapper = new QueryWrapper<>();
			queryWrapper.likeLeft("artwork_id", i);
			List<Artwork> artworks = artworkMapper.selectList(queryWrapper);
			for (Artwork artwork :
					artworks) {
				artworkMapper.deleteById(artwork.getArtworkId());
				try	{
					artworkMapper.execute("INSERT INTO `artwork_" + i + "` VALUES (" + artwork.getArtworkId() + ",'" + artwork.getArtworkTitle() + "'," + artwork.getAuthorId() + ",'" + artwork.getAuthorName() + "'," + (artwork.isHasSeries() ? 1 : 0) + ",'" + artwork.getDescription() + "','" +artwork.getIllustUrlSmall() + "','" + artwork.getIllustUrlOriginal() + "','" + artwork.getIllustUrlMini() + "','" + artwork.getIllustUrlThumb() + "','" + artwork.getIllustUrlRegular() + "'," + artwork.getPageCount() + "," + artwork.getBookmarkCount() + "," + artwork.getLikeCount() + "," + artwork.getViewCount() + ",'" + artwork.getGrading().name() + "','" + artwork.getTags() + "','" + simpleDateFormat.format(artwork.getCreateTime()) + "','" + simpleDateFormat.format(artwork.getUpdateTime()) + "','" + simpleDateFormat.format(artwork.getPatchTime()) + "'," + artwork.getSeriesId() + "," + artwork.getSeriesOrder() + "," + artwork.getNextArtworkId() + "," + ((artwork.getNextArtworkTitle() == null) ? "NULL" : artwork.getNextArtworkTitle()) + "," + artwork.getPreviousArtworkId() + "," + ((artwork.getPreviousArtworkTitle() == null) ? "NULL" : artwork.getPreviousArtworkTitle()) + ")");
				} catch (Exception e) {
					fileOutputStream.write(String.valueOf(artwork.getArtworkId()).getBytes());
					fileOutputStream.write("\n".getBytes());
					fileOutputStream.flush();
					System.out.println(artwork.getArtworkId());
				}
			}
		}
		fileOutputStream.close();

	}

}
