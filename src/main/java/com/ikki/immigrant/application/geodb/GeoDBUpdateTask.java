package com.ikki.immigrant.application.geodb;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.io.*;

/**
 * @author ikki
 */
@Component
@Slf4j
public class GeoDBUpdateTask {

    private final File dbFile;
    private final File dbPath;
    private final RestTemplate restTemplate;
    private final String licenseKey;
    private final String checksumUrl;
    private final String downloadUrl;
    GeoDBService geoDBService;
    private volatile boolean ready;

    public GeoDBUpdateTask(
            RestTemplate restTemplate,
            GeoDBService geoDBService,
            @Value("${geo.db-path:#{T(java.lang.System).getProperty('java.io.tmpdir')}}") String tempPath,
            @Value("${geo.download-url:https://download.maxmind.com/app/geoip_download?edition_id=GeoLite2-City&license_key={licenseKey}&suffix=tar.gz}") String downloadUrl,
            @Value("${geo.checksum-url:https://download.maxmind.com/app/geoip_download?edition_id=GeoLite2-City&license_key={licenseKey}&suffix=tar.gz.sha256}") String checksumUrl,
            @Value("${geo.license-key}") String licenseKey
    ) {
        this.restTemplate = restTemplate;
        this.geoDBService = geoDBService;
        dbPath = new File(tempPath);
        if (!dbPath.exists()) {
            dbPath.mkdirs();
        }
        dbFile = new File(dbPath, "GeoLite2-City.mmdb");
        this.downloadUrl = downloadUrl;
        this.licenseKey = licenseKey;
        this.checksumUrl = checksumUrl;
    }

    public boolean isReady() {
        return ready;
    }

    @PostConstruct
    public void init() {
        ready = dbFile.exists();
        if (!ready) {
            downloadAndCheck();
            geoDBService.reload();
        }
    }

    private void downloadAndCheck() {
        File f = download();
        if (checksum(f)) {
            if (!dbFile.delete()) {
                log.warn("remove old db file  [{}]failed", dbFile.getAbsolutePath());
            }
            ready = decompress(f) && f.delete();
        }
    }

    public boolean dbFileExist() {
        return dbFile.exists();
    }

    public File download() {
        Resource resource = restTemplate.getForObject(downloadUrl, Resource.class, licenseKey);
        if (null == resource) {
            throw new RestClientException(String.format("download maxmind db from %s failed", downloadUrl));
        }
        String name = resource.getFilename();
        File file = new File(dbPath.getPath() + File.separator + name);
        try (InputStream inputStream = resource.getInputStream();
             OutputStream outputStream = new FileOutputStream(file)
        ) {
            int len = -1;
            byte[] buf = new byte[4096];
            while ((len = inputStream.read(buf)) != -1) {
                outputStream.write(buf, 0, len);
            }
            outputStream.flush();
        } catch (IOException e) {
            log.error("download {} file error", name);
        }
        return file;
    }


    private boolean checksum(File file) {
        String checksum = restTemplate.getForObject(checksumUrl, String.class, licenseKey);
        if (!StringUtils.hasLength(checksum)) {
            throw new RestClientException(String.format("download chesksum  from %s failed", checksumUrl));
        }
        checksum = checksum.split(" ")[0];
        try (InputStream inputStream = new FileInputStream(file)) {
            String myChecksum = DigestUtils.sha256Hex(inputStream);
            return checksum.equals(myChecksum);
        } catch (IOException e) {
            log.error("{}", e.getMessage());
            return false;
        }
    }

    private boolean decompress(File file) {
        try (TarArchiveInputStream fin = new TarArchiveInputStream(new GzipCompressorInputStream(new FileInputStream(file)))) {
            TarArchiveEntry entry;
            while ((entry = fin.getNextTarEntry()) != null) {
                if (entry.isDirectory()) {
                    continue;
                }
                if (entry.getName().endsWith("mmdb")) {
                    IOUtils.copy(fin, new FileOutputStream(dbFile));
                }
            }
            return true;
        } catch (IOException e) {
            log.error("{}", e.getMessage());
            return false;
        }
    }

    /**
     * every wednesday in 1:00 clock<br/>
     * New versions are released every Tuesday GMT
     * <p>
     * download limit: 2000 times/24 hours <br/>
     * you can use your own file-server to transfer mmdb file
     *
     * @see <a href="https://support.maxmind.com/geolite-faq/general/is-there-a-limit-to-how-often-i-can-download-a-database-from-my-maxmind-account/">
     * how often i can download a database from my maxmind account
     * </a>
     */
    @Scheduled(cron = "0 0 1 ? * WED", zone = "GMT")
    public void updateDB() {
        downloadAndCheck();
        geoDBService.reload();
    }
}
