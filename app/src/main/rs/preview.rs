#pragma version(1)
#pragma rs_fp_relaxed
#pragma rs java_package_name(com.tools.louis.preview360)

float2 Size;

rs_allocation texture;
rs_sampler sampler;

const float3 cPos = {0.0, 0.0, 0.0};
const float3 cDir = {0.0, 0.0, -1.0};
const float3 cUp = {0.0, 1.0, 0.0};
const float targetDepth = 1.0;
const float3 lightDirection = {-0.577, 0.577, 0.577};

#define M_PI 3.14159265358979323846

typedef struct intersection
{
	float t;
	float hit;
	float3 hitPoint;
	float3 normal;
} Intersection;

typedef struct sphere
{
	float3 position;
	float radius;
} Sphere;

Sphere sphere;

static void intersectSphere(float3 ray, Sphere s, Intersection *i)
{
	float3 a = cPos - s.position;
	float b = dot(a, ray);
	float c = dot(a, a) - (s.radius * s.radius);
	float d = b * b - c;
	if (d > 0.0)
	{
		float t = -b - sqrt(d);
		if (t < i->t)
		{
			i->t = t;
			i->hit = 1.0;
			i->hitPoint.x = cPos.x + ray.x * t;
			i->hitPoint.y = cPos.y + ray.y * t;
			i->hitPoint.z = cPos.z + ray.z * t;
			i->normal = normalize(i->hitPoint - s.position);
		}
	}
}

static void intersect(float3 ray, Intersection *i)
{
	intersectSphere(ray, sphere, i);
}

uchar4 __attribute__((kernel)) grayscale(uchar4 pixelIn, uint32_t x, uint32_t y)
{
	uchar4 pixelOut;


	float2 fragCoord = {x, y};

	float2 p = (fragCoord.xy * 2.0 - Size) / min(Size.x, Size.y);

	// ray init
	float3 cSide = cross(cDir, cUp);
	float3 ray = normalize(cSide * p.x + cUp * p.y + cDir * targetDepth);

	// sphere init
	sphere.position.x = 0;
	sphere.position.y = 0;
	sphere.position.z = 0;

	sphere.radius = 1.0;

	// intersect init
	Intersection i;
	i.t = 1.0e+30;
	i.hit = 0.0;

	// check
	intersect(ray, &i);
	if (i.hit > 0.0)
	{

		float3 vn = {0, 1, 0};
		float3 ve = {-1, 0, 0};

		float phi = acos(-dot(vn, i.normal));

		float u;
		float v = phi / M_PI;

		float theta = (acos(dot(i.normal, ve) / sin(phi))) / (2.0 * M_PI);
		if (dot(cross(vn, ve), i.normal) > 0.0)
		{
			u = theta;
		}
		else
		{
			u = 1.0 - theta;
		}

        float2 texCoord;

        texCoord.x = u;
        texCoord.y = 1.0 - v;

        float4 s = rsSample(texture, sampler, texCoord);
        pixelOut = rsPackColorTo8888(s);


		//       	fragColor = float4(texture(iChannel0, float2(u, v)).rgb, 1.0);
	}
	else
	{
		pixelOut.a = 255;
		pixelOut.r = 0;
		pixelOut.g = 0;
		pixelOut.b = 0;
	}

	return pixelOut;

}

void init()
{
	rsDebug("Hello 2", 0);
}