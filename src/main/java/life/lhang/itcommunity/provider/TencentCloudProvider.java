package life.lhang.itcommunity.provider;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.exception.CosClientException;
import com.qcloud.cos.exception.CosServiceException;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.region.Region;
import life.lhang.itcommunity.exception.CustomizeErrorCode;
import life.lhang.itcommunity.exception.CustomizeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.UUID;

/**
 * Created by codedrinker on 2019/6/28.
 */
@Service
@Slf4j
public class TencentCloudProvider {
    @Value("${qcloud.cos.secretId}")
    private String secretId;

    @Value("${qcloud.cos.secretKey}")
    private String secretKey;

    @Value("${qcloud.cos.bucket-name}")
    private String bucketName;

    @Value("${qcloud.cos.region}")
    private String regionName;

    @Value("${qcloud.cos.directory}")
    private String directory;

    public String upload(MultipartFile file, String fileName) {
        //1.接收文上传的文件，并重命名
        String generatedFileName;
        String[] filePaths = fileName.split("\\.");
        if (filePaths.length > 1) {
            generatedFileName = UUID.randomUUID().toString() + "." + filePaths[filePaths.length - 1];
        } else {
            throw new CustomizeException(CustomizeErrorCode.FILE_UPLOAD_FAIL);
        }

        //2.初始化客户端。COSClient 是调用 COS API 接口的对象。
        //2.1初始化用户身份信息（secretId, secretKey）
        COSCredentials cred = new BasicCOSCredentials(secretId, secretKey);
        //2.2 设置 bucket 的区域
        Region region = new Region(regionName);
        ClientConfig clientConfig = new ClientConfig(region);
        //2.3 生成 cos 客户端。
        COSClient cosClient = new COSClient(cred, clientConfig);

        //3.上传文件
        File localFile = null;
        try {
            // 3.1MultipartFile转File
            localFile = File.createTempFile("temp",null);
            file.transferTo(localFile);
            // 3.2指定要上传到 COS 上对象键(路径)
            String key = directory+"/"+generatedFileName;
            // 3.3上传
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key, localFile);
            cosClient.putObject(putObjectRequest);
            // 3.4返回URL
            Date expiration = new Date(new Date().getTime() + 5 * 60 * 10000);
            String url = cosClient.generatePresignedUrl(bucketName, key, expiration).toString();
            return url;
        } catch (CosServiceException serverException) {
            log.error("upload error,{}", fileName, serverException);
            throw new CustomizeException(CustomizeErrorCode.FILE_UPLOAD_FAIL);
        } catch (CosClientException clientException) {
            log.error("upload error,{}", fileName, clientException);
            throw new CustomizeException(CustomizeErrorCode.FILE_UPLOAD_FAIL);
        } catch (IOException e) {
            log.error("upload error,{}", fileName, e);
            throw new CustomizeException(CustomizeErrorCode.FILE_UPLOAD_FAIL);
        } finally {
            // 关闭客户端(关闭后台线程)
            cosClient.shutdown();
        }

    }
}
