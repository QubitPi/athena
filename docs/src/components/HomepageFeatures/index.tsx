import clsx from 'clsx';
import Heading from '@theme/Heading';
import styles from './styles.module.css';

type FeatureItem = {
  title: string;
  Svg: React.ComponentType<React.ComponentProps<'svg'>>;
  description: JSX.Element;
};

const FeatureList: FeatureItem[] = [
  {
    title: 'Cutting-Edge API Technology',
    Svg: require('@site/static/img/graphql.svg').default,
    description: (
      <>
        Athena makes it easy to build and maintain GraphQL web services for managing file metadata. Athena's API is
        clean and user-friendly, hiding the complexities of complicated data storage and query optimization from
        end-users
      </>
    ),
  },
  {
    title: 'Specialized in Object Storage',
    Svg: require('@site/static/img/object-storage.svg').default,
    description: (
      <>
        Designed for Big Data and scalability, Athena has first-class support for OpenStack Swift and Hadoop HDFS as
        back-ends and flexible pipeline-style architecture which handles nearly any back-end for data storage
      </>
    ),
  },
  {
    title: 'Cloud Native in Nature',
    Svg: require('@site/static/img/openstack.svg').default,
    description: (
      <>
        The long-term goal of Athena is to make it part of OpenStack ecosystem so to make it a quickly-provisioned and
        easily-managed SaaS infrastructural component
      </>
    ),
  },
];

function Feature({title, Svg, description}: FeatureItem) {
  return (
    <div className={clsx('col col--4')}>
      <div className="text--center">
        <Svg className={styles.featureSvg} role="img" />
      </div>
      <div className="text--center padding-horiz--md">
        <Heading as="h3">{title}</Heading>
        <p>{description}</p>
      </div>
    </div>
  );
}

export default function HomepageFeatures(): JSX.Element {
  return (
    <section className={styles.features}>
      <div className="container">
        <div className="row">
          {FeatureList.map((props, idx) => (
            <Feature key={idx} {...props} />
          ))}
        </div>
      </div>
    </section>
  );
}
